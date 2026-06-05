package net.dankito.sitemap

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isInstanceOf
import assertk.assertions.isTrue
import net.dankito.sitemap.model.SitemapResult
import kotlin.test.Test

class JacksonSitemapXmlParserTest {

    private val underTest = JacksonSitemapXmlParser()


    @Test
    fun parse() {
        val sitemapXml = JacksonSitemapXmlParserTest::class.java.classLoader.getResource("testFiles/sitemaps/heise_sitemap.xml")!!.readText()

        val result = underTest.parse(sitemapXml, "https://www.heise.de/sitemap.xml")

        assertThat(result).isInstanceOf<SitemapResult.UrlSet>()
        val urls = (result as SitemapResult.UrlSet).urls
        assertThat(urls).hasSize(10_000)
    }

    @Test
    fun parseImageElements() {
        val sitemapXml = JacksonSitemapXmlParserTest::class.java.classLoader.getResource("testFiles/sitemaps/heise_sitemap-stories-latest.xml")!!.readText()

        val result = underTest.parse(sitemapXml, "https://www.heise.de/bestenlisten/sitemap-stories-latest.xml.gz")

        assertThat(result).isInstanceOf<SitemapResult.UrlSet>()
        val urls = (result as SitemapResult.UrlSet).urls
        assertThat(urls).hasSize(2)
        assertThat(urls.all { it.image != null }).isTrue()
    }

}