package net.dankito.sitemap.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapIndexEntryDto(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: String? = null,
) {
    override fun toString() = location
}