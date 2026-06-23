package net.dankito.sitemap.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.OffsetDateTime

data class SitemapNews(
    val publication: NewsPublication,

    @field:JsonProperty("publication_date")
    val publicationDate: OffsetDateTime,

    val title: String,

    /** "Subscription" or "Registration" */
    val access: String? = null,

    /** Comma-separated: PressRelease, Blog, OpEd, Opinion, UserGenerated */
    val genres: String? = null,

    /** Comma-separated keywords */
    val keywords: String? = null,

    /** Comma-separated stock tickers, max 5 */
    @field:JsonProperty("stock_tickers")
    val stockTickers: String? = null,
) {
    override fun toString() = "${access?.let { "$it: " } ?: ""}$title"
}

data class NewsPublication(
    val name: String,
    /** BCP 47 language code, e.g. "de", "en" */
    val language: String,
)