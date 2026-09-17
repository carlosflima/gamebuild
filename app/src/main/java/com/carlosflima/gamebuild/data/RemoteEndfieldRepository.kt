package com.carlosflima.gamebuild.data

import android.content.Context
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File

internal class RemoteEndfieldRepository(
    private val cache: EndfieldCatalogCache,
    private val download: suspend () -> String,
    private val local: GameRepository = LocalGameRepository(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GameRepository {
    private val mutex = Mutex()
    private var cacheLoaded = false

    @Volatile
    private var catalog: EndfieldCatalogDocument? = null

    override val endfieldCatalogDate: String?
        get() = catalog?.publishedAt

    override fun getCharacters(game: Game): List<GameCharacter> = local.getCharacters(game)

    override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> =
        if (game == Game.ENDFIELD) {
            catalog?.builds?.get(characterId)?.let(::listOf) ?: local.getBuilds(game, characterId)
        } else {
            local.getBuilds(game, characterId)
        }

    override suspend fun loadCachedEndfieldBuilds() = withContext(ioDispatcher) {
        mutex.withLock { loadCacheOnce() }
    }

    override suspend fun refreshEndfieldBuilds(): Boolean = withContext(ioDispatcher) {
        mutex.withLock {
            loadCacheOnce()
            try {
                val json = download()
                currentCoroutineContext().ensureActive()
                val remote = EndfieldCatalogParser.parse(json) ?: return@withLock false
                val previous = catalog
                if (previous != null && remote.revision <= previous.revision) {
                    return@withLock remote == previous
                }
                // Publish only after the complete, validated document is saved.
                cache.write(json)
                catalog = remote
                true
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun loadCacheOnce() {
        if (cacheLoaded) return
        cacheLoaded = true
        catalog = runCatching { cache.read()?.let(EndfieldCatalogParser::parse) }.getOrNull()
    }

    companion object {
        fun create(context: Context): RemoteEndfieldRepository = RemoteEndfieldRepository(
            cache = FileEndfieldCatalogCache(
                File(context.applicationContext.filesDir, "endfield-builds-v1-cache.json")
            ),
            download = { EndfieldCatalogDownload.fetch() }
        )
    }
}
