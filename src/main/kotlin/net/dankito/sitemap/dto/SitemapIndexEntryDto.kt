package net.dankito.sitemap.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class SitemapIndexEntryDto(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: OffsetDateTime? = null,
) {
    override fun toString() = location
}