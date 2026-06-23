package net.dankito.sitemap.model

sealed class SitemapParseResult {
    data class Index(val sourceUrl: String, val referencedUrls: List<SitemapRef>) : SitemapParseResult()
    data class UrlSet(val sourceUrl: String, val urls: List<UrlEntry>) : SitemapParseResult()
    data class Failure(val sourceUrl: String, val reason: String) : SitemapParseResult()
}