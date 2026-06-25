package net.dankito.sitemap

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import net.dankito.sitemap.dto.SitemapIndexDto
import net.dankito.sitemap.dto.SitemapUrlDto
import net.dankito.sitemap.dto.UrlSetDto
import net.dankito.sitemap.model.SitemapIndexEntry
import net.dankito.sitemap.model.SitemapParseResult
import net.dankito.sitemap.model.SitemapUrl
import java.time.Instant
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneOffset

open class JacksonSitemapXmlParser(
    protected val xmlMapper: XmlMapper = XmlMapper().apply {
        registerKotlinModule()
        findAndRegisterModules()

        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
) : SitemapXmlParser() {

    override fun parseSitemapIndex(xml: String, sourceUrl: String): SitemapParseResult {
        val index = xmlMapper.readValue(xml, SitemapIndexDto::class.java)
        return SitemapParseResult.Index(sourceUrl, index.sitemaps.map { SitemapIndexEntry(it.location, it.lastModified?.toInstant()) })
    }

    override fun parseUrlSet(xml: String, sourceUrl: String): SitemapParseResult {
        val urlSet = xmlMapper.readValue(xml, UrlSetDto::class.java)

        return SitemapParseResult.UrlSet(sourceUrl, urlSet.urls.map { mapSitemapUrl(it) })
    }

    private fun mapSitemapUrl(dto: SitemapUrlDto) = SitemapUrl(
        location = dto.location,
        lastModified = mapDateOrOffsetDateTime(dto.lastModified),
        changeFrequency = dto.changeFrequency,

        priority = dto.priority,

        news = dto.news,
        image = dto.image,
        video = dto.video,
    )

    private fun mapDateOrOffsetDateTime(lastModified: String?): Instant? = lastModified?.let {
        if (lastModified.length == 10) {
            LocalDate.parse(lastModified).atStartOfDay().toInstant(ZoneOffset.UTC)
        } else {
            OffsetDateTime.parse(lastModified).toInstant()
        }
    }

}