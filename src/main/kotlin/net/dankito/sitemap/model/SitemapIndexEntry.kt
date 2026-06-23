package net.dankito.sitemap.model

import java.time.Instant

data class SitemapIndexEntry(
    val url: String,
    val lastModified: Instant? = null,
)
