package net.dankito.sitemap.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import net.dankito.sitemap.model.SitemapIndexEntry

@JacksonXmlRootElement(localName = "sitemapindex")
data class SitemapIndexDto(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "sitemap")
    val sitemaps: List<SitemapIndexEntry> = emptyList(),
)