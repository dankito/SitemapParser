package net.dankito.sitemap.diff

import net.dankito.sitemap.model.*
import net.dankito.sitemap.diff.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

class SitemapDiffServiceTest {

    private val underTest = SitemapDiffService()


    // ── shouldSkipFetch ───────────────────────────────────────────────────────

    @Test
    fun `skip when both lastmod set and equal`() {
        val ts = instant(1000)
        val prev = snapshot(fileSnapshot("https://example.com/sitemap.xml", lastModified = ts))
        val ref = SitemapIndexEntry(url = "https://example.com/sitemap.xml", lastModified = ts)
        assertTrue(underTest.shouldSkipFetch(ref, prev))
    }

    @Test
    fun `fetch when lastmod differs`() {
        val prev = snapshot(fileSnapshot("https://example.com/sitemap.xml", lastModified = instant(1000)))
        val ref = SitemapIndexEntry(url = "https://example.com/sitemap.xml", lastModified = instant(2000))
        assertFalse(underTest.shouldSkipFetch(ref, prev))
    }

    @Test
    fun `fetch when ref lastmod is null`() {
        val prev = snapshot(fileSnapshot("https://example.com/sitemap.xml", lastModified = instant(1000)))
        val ref = SitemapIndexEntry(url = "https://example.com/sitemap.xml", lastModified = null)
        assertFalse(underTest.shouldSkipFetch(ref, prev))
    }

    @Test
    fun `fetch when previous lastmod is null`() {
        val prev = snapshot(fileSnapshot("https://example.com/sitemap.xml", lastModified = null))
        val ref = SitemapIndexEntry(url = "https://example.com/sitemap.xml", lastModified = instant(1000))
        assertFalse(underTest.shouldSkipFetch(ref, prev))
    }

    @Test
    fun `fetch when file not in snapshot`() {
        val prev = snapshot()
        val ref = SitemapIndexEntry(url = "https://example.com/sitemap.xml", lastModified = instant(1000))
        assertFalse(underTest.shouldSkipFetch(ref, prev))
    }

    // ── diff – neues UrlSet ───────────────────────────────────────────────────

    @Test
    fun `new sitemap file produces all URLs as new`() {
        val prev = snapshot()
        val current = listOf(
            SitemapParseResult.UrlSet(
                sourceUrl = "https://example.com/sitemap.xml",
                urls = listOf(urlEntry("https://example.com/a"), urlEntry("https://example.com/b")),
            )
        )
        val diff = underTest.diff(prev, current)
        assertEquals(listOf("https://example.com/sitemap.xml"), diff.newSitemapFiles)
        assertEquals(2, diff.newUrls.size)
        assertTrue(diff.updatedUrls.isEmpty())
    }

    @Test
    fun `new URL in known file is detected`() {
        val ts = odt(1000)
        val prev = snapshot(
            fileSnapshot("https://example.com/sitemap.xml", urls = arrayOf(urlSnapshot("https://example.com/a", ts)))
        )
        val current = listOf(
            SitemapParseResult.UrlSet(
                sourceUrl = "https://example.com/sitemap.xml",
                urls = listOf(urlEntry("https://example.com/a", ts), urlEntry("https://example.com/b")),
            )
        )
        val diff = underTest.diff(prev, current)
        assertTrue(diff.newSitemapFiles.isEmpty())
        assertEquals(listOf("https://example.com/b"), diff.newUrls.map { it.location })
        assertTrue(diff.updatedUrls.isEmpty())
    }

    @Test
    fun `newer lastmod marks URL as updated`() {
        val prev = snapshot(
            fileSnapshot(
                "https://example.com/sitemap.xml",
                urls = arrayOf(urlSnapshot("https://example.com/a", odt(1000)))
            )
        )
        val current = listOf(
            SitemapParseResult.UrlSet(
                sourceUrl = "https://example.com/sitemap.xml",
                urls = listOf(urlEntry("https://example.com/a", odt(2000))),
            )
        )
        val diff = underTest.diff(prev, current)
        assertTrue(diff.newUrls.isEmpty())
        assertEquals(listOf("https://example.com/a"), diff.updatedUrls.map { it.location })
    }

    @Test
    fun `same lastmod means URL is not updated`() {
        val ts = odt(1000)
        val prev = snapshot(
            fileSnapshot("https://example.com/sitemap.xml", urls = arrayOf(urlSnapshot("https://example.com/a", ts)))
        )
        val current = listOf(
            SitemapParseResult.UrlSet(
                sourceUrl = "https://example.com/sitemap.xml",
                urls = listOf(urlEntry("https://example.com/a", ts)),
            )
        )
        val diff = underTest.diff(prev, current)
        assertTrue(diff.newUrls.isEmpty())
        assertTrue(diff.updatedUrls.isEmpty())
    }

    @Test
    fun `both lastmod null counts as updated`() {
        val prev = snapshot(
            fileSnapshot("https://example.com/sitemap.xml", urls = arrayOf(urlSnapshot("https://example.com/a", null)))
        )
        val current = listOf(
            SitemapParseResult.UrlSet(
                sourceUrl = "https://example.com/sitemap.xml",
                urls = listOf(urlEntry("https://example.com/a", null)),
            )
        )
        val diff = underTest.diff(prev, current)
        assertTrue(diff.newUrls.isEmpty())
        assertEquals(1, diff.updatedUrls.size)
    }

    // ── diff – sonstiges ─────────────────────────────────────────────────────

    @Test
    fun `Index and Failure results are ignored`() {
        val prev = snapshot()
        val current = listOf(
            SitemapParseResult.Index(
                sourceUrl = "https://example.com/sitemap-index.xml",
                referencedUrls = listOf(SitemapIndexEntry("https://example.com/sitemap.xml")),
            ),
            SitemapParseResult.Failure(
                sourceUrl = "https://example.com/broken.xml",
                reason = "404",
            ),
        )
        val diff = underTest.diff(prev, current)
        assertTrue(diff.newSitemapFiles.isEmpty())
        assertTrue(diff.newUrls.isEmpty())
        assertTrue(diff.updatedUrls.isEmpty())
    }

    @Test
    fun `empty result list produces empty diff`() {
        val prev = snapshot()
        val diff = underTest.diff(prev, emptyList())
        assertTrue(diff.newSitemapFiles.isEmpty())
        assertTrue(diff.newUrls.isEmpty())
        assertTrue(diff.updatedUrls.isEmpty())
    }



    private fun instant(epochSecond: Long) = Instant.ofEpochSecond(epochSecond)
    private fun odt(epochSecond: Long) =
        OffsetDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneOffset.UTC)

    private fun urlEntry(loc: String, lastModified: OffsetDateTime? = null) =
        SitemapUrl(location = loc, lastModified = lastModified)

    private fun snapshot(vararg files: SitemapFileSnapshot) = SitemapSnapshot(
        capturedAt = instant(0),
        sitemapFiles = files.associateBy { it.sourceUrl },
    )

    private fun fileSnapshot(
        url: String,
        lastModified: Instant? = null,
        vararg urls: UrlSnapshot,
    ) = SitemapFileSnapshot(
        sourceUrl = url,
        lastModified = lastModified,
        urls = urls.associateBy { it.location },
    )

    private fun urlSnapshot(location: String, lastModified: OffsetDateTime? = null) =
        UrlSnapshot(location = location, lastModified = lastModified)

}