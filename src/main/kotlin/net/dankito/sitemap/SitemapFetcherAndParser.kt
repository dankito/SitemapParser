package net.dankito.sitemap

import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapResult
import net.dankito.web.client.WebClient
import net.dankito.web.client.get

class SitemapFetcherAndParser(
    protected val xmlParser: SitemapXmlParser = DefaultInstances.sitemapXmlParser,
    protected val webClient: WebClient = DefaultInstances.webClient,
) {

    protected val log by logger()


    suspend fun fetchAndParse(sitemapUrl: String): SitemapResult {
        log.info { "Fetching sitemap: $sitemapUrl" }

        val response = webClient.get<String>(sitemapUrl)

        return if (response.successfulAndBodySet) {
            xmlParser.parse(response.body!!, sitemapUrl)
        } else {
            log.warn(response.error) { "Failed to fetch Sitemap from $sitemapUrl" }
            SitemapResult.Failure(sitemapUrl, response.error?.message ?: "unknown error")
        }
    }

}