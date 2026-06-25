package net.dankito.sitemap.diff.model

import java.time.Instant

data class UrlSnapshot(
    val location: String,
    val lastModified: Instant?,
)