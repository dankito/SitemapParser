package net.dankito.sitemap

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dankito.sitemap.dto.SitemapIndexDto
import net.dankito.sitemap.dto.UrlSetDto
import net.dankito.sitemap.model.SitemapRef
import net.dankito.sitemap.model.SitemapParseResult

open class JacksonSitemapXmlParser(
    protected val xmlMapper: XmlMapper = XmlMapper().apply {
        registerKotlinModule()
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
) : SitemapXmlParser() {

    override fun parseSitemapIndex(xml: String, sourceUrl: String): SitemapParseResult {
        val index = xmlMapper.readValue(xml, SitemapIndexDto::class.java)
        return SitemapParseResult.Index(sourceUrl, index.sitemaps.map { SitemapRef(it.location, it.lastModified?.toInstant()) })
    }

    override fun parseUrlSet(xml: String, sourceUrl: String): SitemapParseResult {
        val urlSet = xmlMapper.readValue(xml, UrlSetDto::class.java)

        return SitemapParseResult.UrlSet(sourceUrl, urlSet.urls)
    }

}