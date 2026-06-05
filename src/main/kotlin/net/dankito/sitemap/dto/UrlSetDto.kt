package net.dankito.sitemap.dto

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import net.dankito.sitemap.model.UrlEntry

@JacksonXmlRootElement(localName = "urlset")
data class UrlSetDto(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "url")
    val urls: List<UrlEntry> = emptyList(),
)