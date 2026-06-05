package net.dankito.sitemap

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dankito.sitemap.dto.SitemapIndexDto
import net.dankito.sitemap.dto.UrlSetDto
import net.dankito.sitemap.model.SitemapResult

open class SitemapXmlParser(
    protected val xmlMapper: XmlMapper = XmlMapper().apply {
        registerKotlinModule()
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
) {

    open fun parse(xml: String, sourceUrl: String): SitemapResult = runCatching {
        when {
            xml.contains("<sitemapindex", ignoreCase = true) -> parseIndex(xml, sourceUrl)
            xml.contains("<urlset", ignoreCase = true) -> parseUrlSet(xml, sourceUrl)
            else -> SitemapResult.Failure(sourceUrl, "Unrecognized XML root element")
        }
    }.getOrElse { SitemapResult.Failure(sourceUrl, "XML parse error: ${it.message}") }


    protected open fun parseIndex(xml: String, sourceUrl: String): SitemapResult {
        val index = xmlMapper.readValue(xml, SitemapIndexDto::class.java)
        return SitemapResult.Index(sourceUrl, index.sitemaps.map { it.location })
    }

    protected open fun parseUrlSet(xml: String, sourceUrl: String): SitemapResult {
        val urlSet = xmlMapper.readValue(xml, UrlSetDto::class.java)

        return SitemapResult.UrlSet(sourceUrl, urlSet.urls)
    }

}