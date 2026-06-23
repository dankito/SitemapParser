package net.dankito.sitemap

import net.dankito.sitemap.model.SitemapParseResult

abstract class SitemapXmlParser {

    protected abstract fun parseSitemapIndex(xml: String, sourceUrl: String): SitemapParseResult

    protected abstract fun parseUrlSet(xml: String, sourceUrl: String): SitemapParseResult


    open fun parse(xml: String, sourceUrl: String): SitemapParseResult = runCatching {
        when {
            xml.contains("<sitemapindex", ignoreCase = true) -> parseSitemapIndex(xml, sourceUrl)
            xml.contains("<urlset", ignoreCase = true) -> parseUrlSet(xml, sourceUrl)
            else -> SitemapParseResult.Failure(sourceUrl, "Unrecognized XML root element")
        }
    }.getOrElse { SitemapParseResult.Failure(sourceUrl, "XML parse error: ${it.message}") }

}