package net.dankito.sitemap.model

sealed class SitemapResult {
    data class Index(val sourceUrl: String, val referencedUrls: List<SitemapRef>) : SitemapResult()
    data class UrlSet(val sourceUrl: String, val urls: List<UrlEntry>) : SitemapResult()
    data class Failure(val sourceUrl: String, val reason: String) : SitemapResult()
}