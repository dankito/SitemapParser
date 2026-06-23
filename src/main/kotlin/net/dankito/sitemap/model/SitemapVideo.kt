package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class SitemapVideo(
    @field:JsonProperty("thumbnail_loc")
    val thumbnailLocation: String? = null,

    val title: String? = null,

    val description: String? = null,

    /**
     * At least one of <video:player_loc> and <video:content_loc> is required. This should be a .mpg, .mpeg, .mp4, .m4v,
     * .mov, .wmv, .asf, .avi, .ra, .ram, .rm, .flv, or other video file format, and can be omitted if <video:player_loc>
     *     is specified. However, because Google needs to be able to check that the Flash object is actually a player
     *     for video (as opposed to some other use of Flash, e.g. games and animations), it's helpful to provide both.
     */
    @field:JsonProperty("content_loc")
    val contentLocation: String? = null,

    /**
     * At least one of content_loc or player_loc is required.
     */
    @field:JsonProperty("player_loc")
    val playerLocation: SitemapVideoPlayerLoc? = null,

    /** Duration in seconds, max 28800 (8h). */
    val duration: Int? = null,

    @field:JsonProperty("expiration_date")
    val expirationDate: OffsetDateTime? = null,

    /** 0.0–5.0 */
    val rating: Double? = null,

    @field:JsonProperty("content_segment_loc")
    val contentSegmentLocations: List<SitemapVideoSegmentLoc> = emptyList(),

    @field:JsonProperty("view_count")
    val viewCount: Long? = null,

    @field:JsonProperty("publication_date")
    val publicationDate: OffsetDateTime? = null,

    /** Max 32 tags. */
    val tags: List<String> = emptyList(),

    /** Max 256 chars. */
    val category: String? = null,

    @field:JsonProperty("family_friendly")
    val familyFriendly: String? = null,

    val restriction: SitemapVideoRestriction? = null,

    @field:JsonProperty("gallery_loc")
    val galleryLocation: SitemapVideoGalleryLoc? = null,

    val prices: List<SitemapVideoPrice> = emptyList(),

    @field:JsonProperty("requires_subscription")
    val requiresSubscription: String? = null,

    val uploader: SitemapVideoUploader? = null,

    val platform: SitemapVideoPlatform? = null,

    val live: String? = null,

    @field:JsonProperty("tvshow")
    val tvShow: SitemapVideoTvShow? = null,

    val ids: List<SitemapVideoId> = emptyList(),
)

data class SitemapVideoPlayerLoc(
    val url: String,
    @field:JsonProperty("allow_embed")
    val allowEmbed: String? = null,
    val autoplay: String? = null,
)

data class SitemapVideoSegmentLoc(
    val url: String,
    /** Duration of this segment in seconds. */
    val duration: Int? = null,
)

/** relationship: "allow" or "deny", value: space-separated ISO 3166 country codes */
data class SitemapVideoRestriction(
    val relationship: String,
    val value: String,
)

data class SitemapVideoGalleryLoc(
    val url: String,
    val title: String? = null,
)

/** type: "purchase"/"rent", resolution: "SD"/"HD", currency: ISO 4217 */
data class SitemapVideoPrice(
    val value: String? = null,
    val currency: String? = null,
    val type: String? = null,
    val resolution: String? = null,
)

data class SitemapVideoUploader(
    val name: String,
    val info: String? = null,
)

/** relationship: "allow" or "deny", value: space-separated "web"/"mobile"/"tv" */
data class SitemapVideoPlatform(
    val relationship: String,
    val value: String,
)

data class SitemapVideoTvShow(
    @field:JsonProperty("show_title")
    val showTitle: String,
    @field:JsonProperty("video_type")
    val videoType: String,
    @field:JsonProperty("episode_title")
    val episodeTitle: String? = null,
    @field:JsonProperty("season_number")
    val seasonNumber: Int? = null,
    @field:JsonProperty("episode_number")
    val episodeNumber: Int? = null,
    @field:JsonProperty("premier_date")
    val premierDate: OffsetDateTime? = null,
)

/** type: "tms:series", "tms:program", "rovi:series", "rovi:program", "freebase", "url" */
data class SitemapVideoId(
    val type: String,
    val value: String,
)