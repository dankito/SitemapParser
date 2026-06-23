package net.dankito.sitemap.model

sealed class SitemapParseResult {
    data class Index(val sourceUrl: String, val referencedUrls: List<SitemapIndexEntry>) : SitemapParseResult()
    data class UrlSet(val sourceUrl: String, val urls: List<SitemapUrl>) : SitemapParseResult()
    data class Failure(val sourceUrl: String, val reason: String) : SitemapParseResult()
}