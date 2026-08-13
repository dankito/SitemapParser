package net.dankito.sitemap

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import net.codinux.log.logger
import net.dankito.sitemap.model.SitemapParseResult
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
        private val StandardPaths = listOf(
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
     * Google doesn't support nested index files, so Sitemap Indices may not contain other Sitemap Indices -> `resolveIndexEntries` =
     * - `false` = only top level indices and sitemaps
     * - `true` = also fetch all sitemaps referenced by sitemap indices
     *
     * @param url Any URL on the target site (e.g. "https://example.com/news/article")
     * @param resolveIndexEntries If sitemap urls in Sitemap Index files should also be resolved (default: true)
     */
    open suspend fun discover(
        url: String,
        resolveIndexEntries: Boolean = true,
        checkStandardPaths: Boolean = true,
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ): List<SitemapParseResult> {
        val origin = URI.create(url).let { URI(it.scheme, it.authority, null, null, null) }.toString()
        val visited = mutableSetOf<String>()
        val results = mutableListOf<SitemapParseResult>()
        val semaphore = Semaphore(5) // to stay below HTTP/2 limit of max 5 concurrent streams

        suspend fun fetchAndParse(sitemapUrl: String, depth: Int) {
            if (sitemapUrl in visited || (resolveIndexEntries == false && depth > 0)) {
                return
            }
            visited += sitemapUrl

            val result = fetchAndParse(sitemapUrl, false)
            results += result
            if (result is SitemapParseResult.Failure) {
                return
            }

            if (result is SitemapParseResult.Index && resolveIndexEntries) {
                result.referencedUrls.map {
                    scope.async { semaphore.withPermit { fetchAndParse(it.url, depth + 1) } }
                }.awaitAll()
            }
        }

        // Step 1: robots.txt
        val robotsUrl = "$origin/robots.txt"
        log.info { "Fetching robots.txt: $robotsUrl" }
        val robotsResponse = webClient.get<String>(robotsUrl)
        val robotsSitemaps = if (robotsResponse.successfulAndBodySet == false) {
            log.warn(robotsResponse.error) { "Could not fetch robots.txt" }
            emptyList()
        } else {
            robotsParser.extractSitemapUrls(robotsResponse.body!!)
        }

        log.info { "Found ${robotsSitemaps.size} sitemap(s) in robots.txt" }
        robotsSitemaps.forEach { fetchAndParse(it, 0) }


        // Step 2: default paths (skipping already-visited)
        if (checkStandardPaths) {
            StandardPaths
                .map { "$origin$it" }
                .filter { it !in visited }
                .filter { webClient.head(RequestParameters(it)).successful }
                .forEach { fetchAndParse(it, 0) }
        }

        return results
    }


    open suspend fun fetchAndParse(sitemapUrl: String, tryToFindNextSitemapPages: Boolean = true): SitemapParseResult =
        sitemapParser.fetchAndParse(sitemapUrl, tryToFindNextSitemapPages)

}