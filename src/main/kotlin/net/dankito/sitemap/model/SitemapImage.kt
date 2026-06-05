package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapImage(
    @field:JsonProperty("loc")
    val location: String,
)