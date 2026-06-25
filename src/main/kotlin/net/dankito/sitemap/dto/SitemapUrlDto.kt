package net.dankito.sitemap.dto

import com.fasterxml.jackson.annotation.JsonProperty
import net.dankito.sitemap.model.SitemapImage
import net.dankito.sitemap.model.SitemapNews
import net.dankito.sitemap.model.SitemapVideo

data class SitemapUrlDto(
    @field:JsonProperty("loc")
    val location: String,

    @field:JsonProperty("lastmod")
    val lastModified: String? = null,

    // one of: always, hourly, daily, weekly, monthly, yearly, never
    @field:JsonProperty("changefreq")
    val changeFrequency: String? = null,

    val priority: Double? = null,

    val news: SitemapNews? = null,
    val image: SitemapImage? = null,
    val video: SitemapVideo? = null,
) {
    override fun toString() = location
}