package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapIndexEntry(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: String? = null,
) {
    override fun toString() = location
}