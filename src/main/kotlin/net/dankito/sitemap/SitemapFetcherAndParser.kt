package net.dankito.sitemap

import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapResult
import net.dankito.web.client.WebClient
import net.dankito.web.client.WebClientResult
import net.dankito.web.client.get
import java.io.InputStream
import java.util.zip.GZIPInputStream

class SitemapFetcherAndParser(
    protected val xmlParser: SitemapXmlParser = DefaultInstances.xmlParser,
    protected val webClient: WebClient = DefaultInstances.webClient,
) {

    protected val log by logger()


    suspend fun fetchAndParse(sitemapUrl: String): SitemapResult {
        log.info { "Fetching sitemap: $sitemapUrl" }

        val response = webClient.get<InputStream>(sitemapUrl)

        return if (response.successfulAndBodySet) {
            val inputStream = if (isGzip(response)) GZIPInputStream(response.body!!)
                              else response.body!!
            xmlParser.parse(inputStream.bufferedReader().readText(), sitemapUrl)
        } else {
            log.warn(response.error) { "Failed to fetch Sitemap from $sitemapUrl" }
            SitemapResult.Failure(sitemapUrl, response.error?.message ?: "unknown error")
        }
    }

    private fun isGzip(response: WebClientResult<InputStream>): Boolean {
        // TODO: a more robust check would be reading the first few bytes of the response body and checking for gzip magic number:
        //   bytes.size >= 2 && bytes[0] == 0x1f.toByte() && bytes[1] == 0x8b.toByte()

        val contentType = response.responseDetails?.contentType ?: return false

        return contentType.startsWith("text/") == false
                && contentType.contains("xml") == false
                && (contentType.startsWith("multipart/archive") || contentType.contains("gzip"))
    }

}