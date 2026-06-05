package net.dankito.sitemap

import net.dankito.sitemap.model.SitemapResult

abstract class SitemapXmlParser {

    protected abstract fun parseSitemapIndex(xml: String, sourceUrl: String): SitemapResult

    protected abstract fun parseUrlSet(xml: String, sourceUrl: String): SitemapResult


    open fun parse(xml: String, sourceUrl: String): SitemapResult = runCatching {
        when {
            xml.contains("<sitemapindex", ignoreCase = true) -> parseSitemapIndex(xml, sourceUrl)
            xml.contains("<urlset", ignoreCase = true) -> parseUrlSet(xml, sourceUrl)
            else -> SitemapResult.Failure(sourceUrl, "Unrecognized XML root element")
        }
    }.getOrElse { SitemapResult.Failure(sourceUrl, "XML parse error: ${it.message}") }

}