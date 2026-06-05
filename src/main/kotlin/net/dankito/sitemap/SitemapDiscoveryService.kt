package net.dankito.sitemap

import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapResult
import net.dankito.web.client.RequestParameters
import net.dankito.web.client.WebClient
import net.dankito.web.client.get
import java.net.URI

open class SitemapDiscoveryService(
    protected val sitemapParser: SitemapFetcherAndParser = DefaultInstances.sitemapParser,
    protected val robotsParser: RobotsTxtParser = DefaultInstances.robotsTxtParser,
    protected val webClient: WebClient = DefaultInstances.webClient,
) {

    companion object {
        private val DEFAULT_PATHS = listOf(
            "/sitemap.xml",
            "/sitemap_index.xml",
            "/sitemapindex.xml",
            "/sitemap-index.xml",
            "/sitemap/sitemap.xml",
            "/news-sitemap.xml",
            "/video-sitemap.xml",
            "/image-sitemap.xml",
        )
    }

    protected val log by logger()

    /**
     * Discovers and parses all sitemaps reachable from the given URL's origin.
     * Checks robots.txt first, then falls back to default paths.
     *
     * @param url Any URL on the target site (e.g. "https://example.com/news/article")
     * @param maxIndexDepth How deep to recurse into sitemap index files (default: 2)
     */
    open suspend fun discover(url: String, maxIndexDepth: Int = 2): List<SitemapResult> {
        val origin = URI.create(url).let { URI(it.scheme, it.authority, null, null, null) }.toString()
        val visited = mutableSetOf<String>()
        val results = mutableListOf<SitemapResult>()

        suspend fun fetchAndParse(sitemapUrl: String, depth: Int) {
            if (sitemapUrl in visited || depth > maxIndexDepth) {
                return
            }
            visited += sitemapUrl

            val result = fetchAndParse(sitemapUrl)
            results += result
            if (result is SitemapResult.Failure) {
                return
            }

            if (result is SitemapResult.Index && depth < maxIndexDepth) {
                result.referencedUrls.forEach { fetchAndParse(it, depth + 1) }
            }
        }

        // Step 1: robots.txt
        val robotsUrl = "$origin/robots.txt"
        log.info { "Fetching robots.txt: $robotsUrl" }
        val robotsResponse = webClient.get<String>(robotsUrl)
        if (robotsResponse.successfulAndBodySet == false) {
            log.warn(robotsResponse.error) { "Could not fetch robots.txt" }
            return emptyList()
        }
        val robotsSitemaps = robotsParser.extractSitemapUrls(robotsResponse.body!!)

        log.info { "Found ${robotsSitemaps.size} sitemap(s) in robots.txt" }
        robotsSitemaps.forEach { fetchAndParse(it, 0) }


        // Step 2: default paths (skipping already-visited)
        DEFAULT_PATHS
            .map { "$origin$it" }
            .filter { it !in visited }
            .filter { webClient.head(RequestParameters(it)).successful }
            .forEach { fetchAndParse(it, 0) }

        return results
    }


    open suspend fun fetchAndParse(sitemapUrl: String): SitemapResult =
        sitemapParser.fetchAndParse(sitemapUrl)

}