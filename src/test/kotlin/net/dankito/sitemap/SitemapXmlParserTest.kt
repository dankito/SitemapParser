package net.dankito.sitemap

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import net.dankito.sitemap.model.SitemapResult
import kotlin.test.Test

class SitemapXmlParserTest {

    private val underTest = SitemapXmlParser()


    @Test
    fun parse() {
        val sitemapXml = SitemapXmlParserTest::class.java.classLoader.getResource("testFiles/sitemaps/heise_sitemap.xml")!!.readText()

        val result = underTest.parse(sitemapXml, "https://www.heise.de/sitemap.xml")

        assertThat(result).isInstanceOf<SitemapResult.UrlSet>()
        val urls = (result as SitemapResult.UrlSet).urls
        assertThat(urls).hasSize(10_000)
    }

}