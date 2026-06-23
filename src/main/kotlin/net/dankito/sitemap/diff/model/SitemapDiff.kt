package net.dankito.sitemap.diff.model

import net.dankito.sitemap.model.SitemapUrl

data class SitemapDiff(
    val newSitemapFiles: List<String>,           // sourceUrls
    val newUrls: List<SitemapUrl>,
    val updatedUrls: List<SitemapUrl>,
)