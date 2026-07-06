package net.dankito.sitemap

import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapParseResult
import net.dankito.web.client.WebClient
import net.dankito.web.client.WebClientResult
import net.dankito.web.client.get
import java.io.InputStream
import java.util.zip.GZIPInputStream

open class SitemapFetcherAndParser(
    protected val xmlParser: SitemapXmlParser = DefaultInstances.xmlParser,
    protected val webClient: WebClient = DefaultInstances.webClient,
) {

    protected val log by logger()


    open suspend fun fetchAndParse(sitemapUrl: String, tryToFindNextSitemapPages: Boolean = true): SitemapParseResult {
        log.info { "Fetching sitemap: $sitemapUrl" }

        // TODO: does not work with KtorWebClient as it does not support InputStream, there we would need to use ByteReadChannel
        val response = webClient.get<InputStream>(sitemapUrl)

        return if (response.successfulAndBodySet) {
            val contentType = response.responseDetails?.contentType?.substringBefore(';')
            val inputStream = if (isGzip(response)) GZIPInputStream(response.body!!)
                            else response.body!!
            val responseBody = inputStream.bufferedReader().readText()

            if (isXml(contentType, responseBody) || inputStream is GZIPInputStream) {
                xmlParser.parse(responseBody, sitemapUrl)
            } else {
                log.warn { "Expected to retrieve 'text/xml' as content type for sitemap but got $contentType" }
                SitemapParseResult.Failure(sitemapUrl, "Unsupported content type: $contentType. Response body: $responseBody")
            }
        } else {
            log.warn(response.error) { "Failed to fetch Sitemap from $sitemapUrl" }
            SitemapParseResult.Failure(sitemapUrl, response.error?.message ?: "unknown error")
        }
    }

    protected open fun isXml(contentType: String?, responseBody: String): Boolean =
        contentType == "text/xml"
                || contentType == "application/xml"
                || responseBody.startsWith("<?xml ")
                || responseBody.startsWith("<sitemapindex ")
                || responseBody.startsWith("<urlset ")

    protected open fun isGzip(response: WebClientResult<InputStream>): Boolean {
        // TODO: a more robust check would be reading the first few bytes of the response body and checking for gzip magic number:
        //   bytes.size >= 2 && bytes[0] == 0x1f.toByte() && bytes[1] == 0x8b.toByte()

        val contentType = response.responseDetails?.contentType ?: return false

        return contentType.startsWith("text/") == false
                && contentType.contains("xml") == false
                && (contentType.startsWith("multipart/archive") || contentType.contains("gzip"))
    }

}