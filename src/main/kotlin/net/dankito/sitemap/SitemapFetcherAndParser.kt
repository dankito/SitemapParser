package net.dankito.sitemap

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapParseResult
import net.dankito.sitemap.model.SitemapUrl
import net.dankito.web.client.WebClient
import net.dankito.web.client.WebClientResult
import net.dankito.web.client.get
import java.io.InputStream
import java.util.concurrent.ConcurrentSkipListSet
import java.util.zip.GZIPInputStream

open class SitemapFetcherAndParser(
    protected val xmlParser: SitemapXmlParser = DefaultInstances.xmlParser,
    protected val webClient: WebClient = DefaultInstances.webClient,
) {

    companion object {
        val PageQueryParam = Regex("""\?page=(\d+)$""")
        val PageInFilename = Regex("""(\d+)\.xml(\.gz)?$""")
    }


    protected val log by logger()


    open suspend fun fetchAndParse(sitemapUrl: String, discoverNextPages: Boolean = true) =
        fetchAndParse(sitemapUrl, discoverNextPages, ConcurrentSkipListSet())

    protected open suspend fun fetchAndParse(sitemapUrl: String, discoverNextPages: Boolean = true, visitedNextPagesUrls: MutableSet<String>): SitemapParseResult {
        log.info { "Fetching sitemap: $sitemapUrl" }

        // TODO: does not work with KtorWebClient as it does not support InputStream, there we would need to use ByteReadChannel
        val response = webClient.get<InputStream>(sitemapUrl)

        return if (response.successfulAndBodySet) {
            val contentType = response.responseDetails?.contentType?.substringBefore(';')
            val inputStream = if (isGzip(response)) GZIPInputStream(response.body!!)
                            else response.body!!
            val responseBody = inputStream.bufferedReader().readText()

            if (isXml(contentType, responseBody) || inputStream is GZIPInputStream) {
                parseResponse(responseBody, sitemapUrl, discoverNextPages, visitedNextPagesUrls)
            } else {
                log.warn { "Expected to retrieve 'text/xml' as content type for sitemap but got $contentType" }
                SitemapParseResult.Failure(sitemapUrl, "Unsupported content type: $contentType. Response body: $responseBody")
            }
        } else {
            log.warn(response.error) { "Failed to fetch Sitemap from $sitemapUrl" }
            SitemapParseResult.Failure(sitemapUrl, response.error?.message ?: "unknown error")
        }
    }


    protected open suspend fun parseResponse(responseBody: String, sitemapUrl: String, discoverNextPages: Boolean = true, visitedNextPagesUrls: MutableSet<String>): SitemapParseResult {
        val result = xmlParser.parse(responseBody, sitemapUrl)

        if (result is SitemapParseResult.UrlSet && discoverNextPages) {
            val nextPagesUrls = getNextPages(sitemapUrl, discoverNextPages, visitedNextPagesUrls)
            if (nextPagesUrls.isNotEmpty()) {
                return SitemapParseResult.UrlSet(sitemapUrl, (result.urls + nextPagesUrls).toSet().toList())
            }
        }

        return result
    }

    protected open suspend fun getNextPages(sitemapUrl: String, discoverNextPages: Boolean, visitedNextPagesUrls: MutableSet<String>): Set<SitemapUrl> {
        val nextPagesUrls = getListOfPossibleNextPagesUrls(sitemapUrl)

        val unvisitedNextPagesUrls = nextPagesUrls.filterNot { visitedNextPagesUrls.contains(it) }
        visitedNextPagesUrls.addAll(unvisitedNextPagesUrls)

        if (unvisitedNextPagesUrls.isEmpty()) {
            return emptySet()
        }
        val nextPagesResults = coroutineScope {
            unvisitedNextPagesUrls.map { url ->
                async { fetchAndParse(url, discoverNextPages, visitedNextPagesUrls) }
            }.awaitAll()
        }

        return nextPagesResults.filterIsInstance<SitemapParseResult.UrlSet>().flatMap { it.urls }.toSet()
    }

    protected open suspend fun getListOfPossibleNextPagesUrls(sitemapUrl: String): List<String> {
        val filename = sitemapUrl.substringAfterLast('/')

        val nextPageFilenames = if (PageQueryParam.containsMatchIn(filename)) {
            val match = PageQueryParam.find(filename)!!
            val page = match.groupValues.get(1).toInt()
            listOf(filename.substringBeforeLast(page.toString()) + (page + 1))
        } else if (PageInFilename.containsMatchIn(filename)) {
            val match = PageInFilename.find(filename)!!
            val page = match.groupValues.get(1).toInt()
            listOf(filename.substringBeforeLast(page.toString()) + (page + 1) + filename.substringAfterLast(page.toString()))
        } else if (filename.endsWith(".xml.gz")) {
            listOf(filename.replace(".xml.gz", "1.xml.gz"), filename + "?page=1")
        } else if (filename.endsWith(".xml")) {
            listOf(filename.replace(".xml", "1.xml"), filename + "?page=1")
        } else {
            emptyList()
        }

        val urlWithoutFilename = sitemapUrl.substringBeforeLast('/')
        return nextPageFilenames.toSet().map { urlWithoutFilename + "/" + it }
    }

    protected open fun isXml(contentType: String?, responseBody: String): Boolean = when (contentType?.lowercase()) {
        "text/xml", "application/xml" -> true
        else -> {
            val bodyTrimmed = responseBody.trimStart().take("<sitemapindex".length).lowercase()
            bodyTrimmed.startsWith("<?xml")
                || bodyTrimmed.startsWith("<sitemapindex")
                || bodyTrimmed.startsWith("<urlset")
        }
    }

    protected open fun isGzip(response: WebClientResult<InputStream>): Boolean {
        // TODO: a more robust check would be reading the first few bytes of the response body and checking for gzip magic number:
        //   bytes.size >= 2 && bytes[0] == 0x1f.toByte() && bytes[1] == 0x8b.toByte()

        val contentType = response.responseDetails?.contentType ?: return false

        return contentType.startsWith("text/") == false
                && contentType.contains("xml") == false
                && (contentType.startsWith("multipart/archive") || contentType.contains("gzip"))
    }

}