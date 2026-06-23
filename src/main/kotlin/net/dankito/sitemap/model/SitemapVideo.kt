package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SitemapVideo(
    val title: String? = null,
    val description: String? = null,
    @field:JsonProperty("thumbnail_loc")
    val thumbnailLocation: String? = null,

    /**
     * At least one of <video:player_loc> and <video:content_loc> is required. This should be a .mpg, .mpeg, .mp4, .m4v,
     * .mov, .wmv, .asf, .avi, .ra, .ram, .rm, .flv, or other video file format, and can be omitted if <video:player_loc>
     *     is specified. However, because Google needs to be able to check that the Flash object is actually a player
     *     for video (as opposed to some other use of Flash, e.g. games and animations), it's helpful to provide both.
     */
    @field:JsonProperty("content_loc")
    val contentLocation: String? = null,
)