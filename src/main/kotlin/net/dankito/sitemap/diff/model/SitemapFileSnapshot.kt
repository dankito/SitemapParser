package net.dankito.sitemap.diff.model

import java.time.Instant

data class SitemapFileSnapshot(
    val sourceUrl: String,
    val lastModified: Instant?,
    val urls: Map<String, UrlSnapshot>, // key = location, empty for index files
)