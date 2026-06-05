package net.dankito.sitemap.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class SitemapIndexEntryDto(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: Instant? = null,
) {
    override fun toString() = location
}