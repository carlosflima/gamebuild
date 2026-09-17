package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RemoteEndfieldRepositoryTest {
    private val characterId = "endfield-endministrator"

    @Test
    fun usesBundledBuildsBeforeLoadingCache() = runTest {
        val repository = RemoteEndfieldRepository(
            MemoryCache(), { error("offline") }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        assertEquals(
            LocalGameRepository().getBuilds(Game.ENDFIELD, characterId),
            repository.getBuilds(Game.ENDFIELD, characterId)
        )
        assertNull(repository.endfieldCatalogDate)
        assertFalse(repository.refreshEndfieldBuilds())
        assertEquals(1, repository.getBuilds(Game.ENDFIELD, characterId).size)
    }

    @Test
    fun loadsValidCacheOfflineAndKeepsItAfterDownloadFailure() = runTest {
        val cache = MemoryCache(changedEndfieldCatalog(2, "Cached"))
        val repository = RemoteEndfieldRepository(
            cache, { error("offline") }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        repository.loadCachedEndfieldBuilds()
        assertEquals("Cached", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
        assertFalse(repository.refreshEndfieldBuilds())
        assertEquals("Cached", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
        assertEquals(0, cache.writes)
    }

    @Test
    fun validUpdateIsSavedAndSurvivesRepositoryRecreation() = runTest {
        val cache = MemoryCache()
        val dispatcher = StandardTestDispatcher(testScheduler)
        val json = changedEndfieldCatalog(2)
        val repository = RemoteEndfieldRepository(cache, { json }, ioDispatcher = dispatcher)
        assertTrue(repository.refreshEndfieldBuilds())
        assertEquals(json, cache.json)
        assertEquals("Updated starter", repository.getBuilds(Game.ENDFIELD, characterId).single().title)

        val recreated = RemoteEndfieldRepository(cache, { error("offline") }, ioDispatcher = dispatcher)
        recreated.loadCachedEndfieldBuilds()
        assertEquals(repository.getBuilds(Game.ENDFIELD, characterId), recreated.getBuilds(Game.ENDFIELD, characterId))
        assertEquals(repository.endfieldCatalogDate, recreated.endfieldCatalogDate)
    }

    @Test
    fun invalidRemoteDoesNotOverwriteValidCacheOrApplyPartialData() = runTest {
        val json = changedEndfieldCatalog(2, "Cached")
        val cache = MemoryCache(json)
        val repository = RemoteEndfieldRepository(
            cache, { """{"schemaVersion":1}""" }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        assertFalse(repository.refreshEndfieldBuilds())
        assertEquals(json, cache.json)
        assertEquals("Cached", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
        assertEquals(0, cache.writes)
    }

    @Test
    fun corruptCacheFallsBackThenRecoversFromRemote() = runTest {
        val cache = MemoryCache("{")
        val repository = RemoteEndfieldRepository(
            cache, { endfieldCatalogJson() }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        repository.loadCachedEndfieldBuilds()
        assertNull(repository.endfieldCatalogDate)
        assertTrue(repository.refreshEndfieldBuilds())
        assertEquals(endfieldCatalogJson(), cache.json)
    }

    @Test
    fun failedCacheWriteKeepsPreviousBuildsAndAllowsRetry() = runTest {
        val cache = MemoryCache(changedEndfieldCatalog(2, "Cached"))
        cache.failWrite = true
        val repository = RemoteEndfieldRepository(
            cache, { changedEndfieldCatalog(3) }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        assertFalse(repository.refreshEndfieldBuilds())
        assertEquals("Cached", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
        cache.failWrite = false
        assertTrue(repository.refreshEndfieldBuilds())
        assertEquals("Updated starter", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
    }

    @Test
    fun refusesRollbackAndReusedRevisionButAcceptsUnchangedDocument() = runTest {
        val json = changedEndfieldCatalog(3, "Current")
        val cache = MemoryCache(json)
        var response = changedEndfieldCatalog(2, "Older")
        val repository = RemoteEndfieldRepository(
            cache, { response }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        assertFalse(repository.refreshEndfieldBuilds())
        response = changedEndfieldCatalog(3, "Changed without revision")
        assertFalse(repository.refreshEndfieldBuilds())
        response = json
        assertTrue(repository.refreshEndfieldBuilds())
        assertEquals("Current", repository.getBuilds(Game.ENDFIELD, characterId).single().title)
        assertEquals(0, cache.writes)
    }

    @Test
    fun keepsOtherGamesAndUnknownCharactersIsolated() = runTest {
        val repository = RemoteEndfieldRepository(
            MemoryCache(), { changedEndfieldCatalog(2) }, ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        assertTrue(repository.refreshEndfieldBuilds())
        val local = LocalGameRepository()
        listOf(Game.NTE, Game.WARFRAME).forEach { game ->
            assertEquals(local.getCharacters(game), repository.getCharacters(game))
            local.getCharacters(game).forEach { character ->
                assertEquals(local.getBuilds(game, character.id), repository.getBuilds(game, character.id))
            }
        }
        assertTrue(repository.getBuilds(Game.ENDFIELD, "unknown").isEmpty())
        assertTrue(repository.getBuilds(Game.WARFRAME, characterId).isEmpty())
    }

    @Test
    fun cancellationDoesNotPublishOrSaveTheDownload() = runTest {
        val cache = MemoryCache()
        val repository = RemoteEndfieldRepository(
            cache, { throw CancellationException("cancelled") },
            ioDispatcher = StandardTestDispatcher(testScheduler)
        )
        var cancelled = false
        try {
            repository.refreshEndfieldBuilds()
        } catch (_: CancellationException) {
            cancelled = true
        }
        assertTrue(cancelled)
        assertNull(cache.json)
        assertEquals(0, cache.writes)
    }

    private class MemoryCache(var json: String? = null) : EndfieldCatalogCache {
        var writes = 0
        var failWrite = false
        override fun read(): String? = json
        override fun write(json: String) {
            if (failWrite) error("storage unavailable")
            writes++
            this.json = json
        }
    }
}
