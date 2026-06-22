package net.dankito.sitemap.model

import java.time.Instant

data class SitemapRef(
    val url: String,
    val lastModified: Instant? = null,
)
