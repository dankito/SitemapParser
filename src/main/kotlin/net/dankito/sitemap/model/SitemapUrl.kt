package net.dankito.sitemap.model

import java.time.Instant

data class SitemapUrl(
    val location: String,

    val lastModified: Instant? = null,

    // one of: always, hourly, daily, weekly, monthly, yearly, never
    val changeFrequency: String? = null,

    val priority: Double? = null,

    val news: SitemapNews? = null,
    val image: SitemapImage? = null,
    val video: SitemapVideo? = null,
) {
    override fun toString() = location
}