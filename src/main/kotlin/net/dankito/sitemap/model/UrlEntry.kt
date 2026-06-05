package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class UrlEntry(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: Instant? = null,
    @field:JsonProperty("changefreq")
    val changeFrequency: String? = null,
    val priority: Double? = null,
    val image: SitemapImage? = null,
    val video: SitemapVideo? = null,
) {
    override fun toString() = location
}