package net.dankito.sitemap.diff

import net.dankito.sitemap.diff.model.*
import net.dankito.sitemap.model.SitemapIndexEntry
import net.dankito.sitemap.model.SitemapParseResult
import net.dankito.sitemap.model.SitemapUrl

class SitemapDiffService {

    fun diff(previous: SitemapSnapshot, current: List<SitemapParseResult>): SitemapDiff {
        val newSitemapFiles = mutableListOf<String>()
        val newUrls = mutableListOf<SitemapUrl>()
        val updatedUrls = mutableListOf<SitemapUrl>()

        for (result in current) {
            when (result) {
                is SitemapParseResult.UrlSet -> {
                    val previousFile = previous.sitemapFiles[result.sourceUrl]
                    if (previousFile == null) {
                        newSitemapFiles.add(result.sourceUrl)
                        newUrls.addAll(result.urls)
                        continue
                    }
                    for (entry in result.urls) {
                        val previousUrl = previousFile.urls[entry.location]
                        when {
                            previousUrl == null -> newUrls.add(entry)
                            hasChanged(previousUrl, entry) -> updatedUrls.add(entry)
                        }
                    }
                }
                is SitemapParseResult.Index -> Unit
                is SitemapParseResult.Failure -> Unit
            }
        }

        return SitemapDiff(newSitemapFiles, newUrls, updatedUrls)
    }

    private fun hasChanged(previous: UrlSnapshot, current: SitemapUrl): Boolean {
        val previousMod = previous.lastModified
        val currentMod = current.lastModified
        if (previousMod != null && currentMod != null) {
            return currentMod > previousMod
        }
        return true // lastmod nicht zuverlässig → immer als geändert betrachten
    }


    fun shouldSkipFetch(ref: SitemapIndexEntry, previous: SitemapSnapshot): Boolean {
        val previousFile = previous.sitemapFiles[ref.url] ?: return false
        val previousMod = previousFile.lastModified
        val currentMod = ref.lastModified
        return previousMod != null && currentMod != null && previousMod == currentMod
    }
}