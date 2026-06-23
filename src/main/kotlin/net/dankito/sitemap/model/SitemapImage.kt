package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapImage(
    /**
     *  The URL of the image.
     */
    @field:JsonProperty("loc")
    val location: String,

    val title: String? = null,
    val caption: String? = null,

    /**
     * The geographic location of the image. For example, "Limerick, Ireland".
     */
    @field:JsonProperty("geo_location")
    val geoLocation: String? = null,

    /**
     * A URL to the license of the image.
     */
    val license: String? = null,
)