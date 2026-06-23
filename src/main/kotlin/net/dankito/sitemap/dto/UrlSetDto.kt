package net.dankito.sitemap.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import net.dankito.sitemap.model.SitemapUrl

@JacksonXmlRootElement(localName = "urlset")
data class UrlSetDto(
    @field:JacksonXmlElementWrapper(useWrapping = false)
    @field:JacksonXmlProperty(localName = "url")
    val urls: List<SitemapUrl> = emptyList(),
)