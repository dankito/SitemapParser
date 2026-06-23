package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class UrlEntry(
    @field:JsonProperty("loc")
    val location: String,

    // TODO: can also be a date like 2005-05-10
    @field:JsonProperty("lastmod")
    val lastModified: OffsetDateTime? = null,

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