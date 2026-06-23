package net.dankito.sitemap

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.dankito.sitemap.model.SitemapParseResult
import kotlin.test.Test

class SitemapDiscoveryServiceTest {

    private val underTest = SitemapDiscoveryService()


    @Test
    fun discover() = runTest {
        val result = underTest.discover("https://www.heise.de")

        assertThat(result.size).isGreaterThan(213)

        // there are 7 sitemap entries in robots.txt:
        // - one of these does not exist (https://www.heise.de/sitemapindex.xml).
        // - one, even if stated otherwise, is a sitemap index file which contains more than 200 sitemap files: https://www.heise.de/bestenlisten/sitemap-articles-index.xml.gz
        // - and sitemap.xml is not in robots.txt but discovered by standard paths. Without it result.size would be 213, so check if its greater
        val failures = result.filterIsInstance<SitemapParseResult.Failure>()
        val sitemapIndices = result.filterIsInstance<SitemapParseResult.Index>()
        val urlSets = result.filterIsInstance<SitemapParseResult.UrlSet>()

        assertThat(failures).hasSize(1)
        assertThat(failures.first().sourceUrl).isEqualTo("https://www.heise.de/sitemapindex.xml")

        assertThat(sitemapIndices).hasSize(1)
        assertThat(sitemapIndices.first().sourceUrl).isEqualTo("https://www.heise.de/bestenlisten/sitemap-articles-index.xml.gz")

        val discoveredSitemap = urlSets.firstOrNull { it.sourceUrl == "https://www.heise.de/sitemap.xml" }
        assertThat(discoveredSitemap).isNotNull()
    }

    @Test
    fun fetchAndParseImageSitemap() = runTest {
        val result = underTest.fetchAndParse("https://www.faz.net/sitemap-wirtschaft-bilder-1.xml")

        assertThat(result).isInstanceOf<SitemapParseResult.UrlSet>()

        val urls = (result as SitemapParseResult.UrlSet).urls
        assertThat(urls.size).isGreaterThanOrEqualTo(1_000)

        val images = urls.filter { it.image != null }
        val noImages = urls.filter { it.image == null }
        assertThat(noImages).isEmpty()
        assertThat(images).hasSize(urls.size)
    }

    @Test
    fun fetchAndParseVideoSitemap() = runTest {
        val result = underTest.fetchAndParse("https://www.faz.net/sitemap-politik-video-1.xml")

        assertThat(result).isInstanceOf<SitemapParseResult.UrlSet>()

        val urls = (result as SitemapParseResult.UrlSet).urls
        assertThat(urls.size).isGreaterThanOrEqualTo(1_500)

        val videos = urls.filter { it.video != null }
        val noVideo = urls.filter { it.video == null }
        assertThat(noVideo).isEmpty()
        assertThat(videos).hasSize(urls.size)
    }

}