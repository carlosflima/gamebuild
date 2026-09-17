package com.carlosflima.gamebuild.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.File

class EndfieldCatalogStorageTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun replacesExistingCacheAndRemovesTemporaryFile() {
        val file = File(temporaryFolder.root, "catalog.json")
        val cache = FileEndfieldCatalogCache(file)
        assertNull(cache.read())
        cache.write(endfieldCatalogJson())
        val updated = changedEndfieldCatalog(2)
        cache.write(updated)
        assertEquals(updated, cache.read())
        assertFalse(File(temporaryFolder.root, "catalog.json.tmp").exists())
    }

    @Test
    fun failedWriteDoesNotTruncateLastValidFile() {
        val file = File(temporaryFolder.root, "catalog.json")
        val cache = FileEndfieldCatalogCache(file)
        val original = endfieldCatalogJson()
        cache.write(original)
        assertTrue(File(temporaryFolder.root, "catalog.json.tmp").mkdir())
        assertTrue(runCatching { cache.write(changedEndfieldCatalog(2)) }.isFailure)
        assertEquals(original, cache.read())
    }

    @Test
    fun rejectsOversizedCacheAndStreamsWithoutTrustingContentLength() {
        val bytes = ByteArray(EndfieldCatalogParser.MAX_DOCUMENT_BYTES + 1) { 65 }
        val file = File(temporaryFolder.root, "catalog.json")
        file.writeBytes(bytes)
        assertNull(FileEndfieldCatalogCache(file).read())
        assertTrue(runCatching { readEndfieldDocument(ByteArrayInputStream(bytes)) }.isFailure)
        val small = "á".toByteArray(Charsets.UTF_8)
        assertEquals("á", readEndfieldDocument(ByteArrayInputStream(small)))
    }
}
