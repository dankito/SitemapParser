package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapVideo(
    val title: String? = null,
    val description: String? = null,
    @field:JsonProperty("thumbnail_loc")
    val thumbnailLocation: String? = null,
    @field:JsonProperty("content_loc")
    val contentLocation: String? = null,
)