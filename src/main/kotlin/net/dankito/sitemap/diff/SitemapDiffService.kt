package net.dankito.sitemap.diff

import net.dankito.sitemap.diff.model.*
import net.dankito.sitemap.model.SitemapRef
import net.dankito.sitemap.model.SitemapResult
import net.dankito.sitemap.model.UrlEntry

class SitemapDiffService {

    fun diff(previous: SitemapSnapshot, current: List<SitemapResult>): SitemapDiff {
        val newSitemapFiles = mutableListOf<String>()
        val newUrls = mutableListOf<UrlEntry>()
        val updatedUrls = mutableListOf<UrlEntry>()

        for (result in current) {
            when (result) {
                is SitemapResult.UrlSet -> {
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
                is SitemapResult.Index -> Unit
                is SitemapResult.Failure -> Unit
            }
        }

        return SitemapDiff(newSitemapFiles, newUrls, updatedUrls)
    }

    private fun hasChanged(previous: UrlSnapshot, current: UrlEntry): Boolean {
        val previousMod = previous.lastModified
        val currentMod = current.lastModified
        if (previousMod != null && currentMod != null) {
            return currentMod.toInstant() > previousMod.toInstant()
        }
        return true // lastmod nicht zuverlässig → immer als geändert betrachten
    }


    fun shouldSkipFetch(ref: SitemapRef, previous: SitemapSnapshot): Boolean {
        val previousFile = previous.sitemapFiles[ref.url] ?: return false
        val previousMod = previousFile.lastModified
        val currentMod = ref.lastModified
        return previousMod != null && currentMod != null && previousMod == currentMod
    }
}