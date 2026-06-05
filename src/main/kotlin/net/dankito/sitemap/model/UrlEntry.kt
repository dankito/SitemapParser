package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UrlEntry(
    @field:JsonProperty("loc")
    val location: String,
    @field:JsonProperty("lastmod")
    val lastModified: String? = null,
    @field:JsonProperty("changefreq")
    val changeFrequency: String? = null,
    val priority: Double? = null,
) {
    override fun toString() = location
}