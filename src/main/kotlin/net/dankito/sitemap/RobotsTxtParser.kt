package net.dankito.sitemap

/**
 * A very simplified version of a robots.txt parser that only extracts sitemap URLs.
 */
open class RobotsTxtParser {

    open fun extractSitemapUrls(content: String): List<String> =
        content.lines()
            .filter { it.trimStart().startsWith("Sitemap:", ignoreCase = true) }
            .mapNotNull { it.substringAfter(":", "").trim().takeIf { url -> url.isNotEmpty() } }

}