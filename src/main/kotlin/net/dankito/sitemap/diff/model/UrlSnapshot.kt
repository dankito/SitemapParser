package net.dankito.sitemap.diff.model

import java.time.OffsetDateTime

data class UrlSnapshot(
    val location: String,
    val lastModified: OffsetDateTime?,
)