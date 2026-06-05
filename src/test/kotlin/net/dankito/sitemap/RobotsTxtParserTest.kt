package net.dankito.sitemap

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.hasSize
import kotlin.test.Test

class RobotsTxtParserTest {

    private val underTest = RobotsTxtParser()


    @Test
    fun extractSitemapUrls() {
        val robotsTxt = RobotsTxtParserTest::class.java.classLoader.getResource("testFiles/robots_txt/heise_robots.txt")!!.readText()

        val result = underTest.extractSitemapUrls(robotsTxt)

        assertThat(result).hasSize(7)
        assertThat(result).containsExactlyInAnyOrder(
            "https://www.heise.de/sitemapindex.xml",
            "https://www.heise.de/bestenlisten/sitemap-articles-index.xml.gz",
            "https://www.heise.de/bestenlisten/sitemap-authors.xml.gz",
            "https://www.heise.de/bestenlisten/sitemap-categories.xml.gz",
            "https://www.heise.de/bestenlisten/sitemap-news.xml.gz",
            "https://www.heise.de/bestenlisten/sitemap-stories-latest.xml.gz",
            "https://www.heise.de/bestenlisten/sitemap-topics.xml.gz"
        )
    }

}