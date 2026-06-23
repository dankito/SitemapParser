package net.dankito.sitemap.diff.model

import net.dankito.sitemap.model.UrlEntry

data class SitemapDiff(
    val newSitemapFiles: List<String>,           // sourceUrls
    val newUrls: List<UrlEntry>,
    val updatedUrls: List<UrlEntry>,
)