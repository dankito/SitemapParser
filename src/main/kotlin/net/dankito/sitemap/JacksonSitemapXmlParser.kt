package net.dankito.sitemap

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dankito.sitemap.dto.SitemapIndexDto
import net.dankito.sitemap.dto.UrlSetDto
import net.dankito.sitemap.model.SitemapResult

open class JacksonSitemapXmlParser(
    protected val xmlMapper: XmlMapper = XmlMapper().apply {
        registerKotlinModule()
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
) : SitemapXmlParser() {

    override fun parseSitemapIndex(xml: String, sourceUrl: String): SitemapResult {
        val index = xmlMapper.readValue(xml, SitemapIndexDto::class.java)
        return SitemapResult.Index(sourceUrl, index.sitemaps.map { it.location })
    }

    override fun parseUrlSet(xml: String, sourceUrl: String): SitemapResult {
        val urlSet = xmlMapper.readValue(xml, UrlSetDto::class.java)

        return SitemapResult.UrlSet(sourceUrl, urlSet.urls)
    }

}