package net.dankito.sitemap

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import assertk.assertions.isNotNull
import kotlinx.coroutines.test.runTest
import net.dankito.sitemap.model.SitemapResult
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
        val failures = result.filterIsInstance<SitemapResult.Failure>()
        val sitemapIndices = result.filterIsInstance<SitemapResult.Index>()
        val urlSets = result.filterIsInstance<SitemapResult.UrlSet>()

        assertThat(failures).hasSize(1)
        assertThat(failures.first().sourceUrl).isEqualTo("https://www.heise.de/sitemapindex.xml")

        assertThat(sitemapIndices).hasSize(1)
        assertThat(sitemapIndices.first().sourceUrl).isEqualTo("https://www.heise.de/bestenlisten/sitemap-articles-index.xml.gz")

        val discoveredSitemap = urlSets.firstOrNull { it.sourceUrl == "https://www.heise.de/sitemap.xml" }
        assertThat(discoveredSitemap).isNotNull()
    }

}