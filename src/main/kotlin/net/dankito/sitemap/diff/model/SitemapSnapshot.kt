package net.dankito.sitemap.diff.model

import java.time.Instant

data class SitemapSnapshot(
    val capturedAt: Instant,
    val sitemapFiles: Map<String, SitemapFileSnapshot>, // key = sourceUrl
)