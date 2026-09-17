package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.data.LocalGameRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.Game
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameBuildCatalogUpdateTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setMain() = Dispatchers.setMain(dispatcher)

    @After
    fun resetMain() = Dispatchers.resetMain()

    @Test
    fun displaysCacheBeforeNetworkAndPreservesDetailSearchAndFilters() = runTest(dispatcher) {
        val repository = UpdatingRepository()
        val viewModel = GameBuildViewModel(repository)
        selectDetail(viewModel)
        viewModel.updateCharacterQuery("End")
        viewModel.toggleCharacterFilter("Physical")
        viewModel.selectBuildType(BuildType.F2P)

        viewModel.initializeCatalog()
        runCurrent()
        assertEquals("Cached", viewModel.uiState.value.builds.single().title)
        assertTrue(viewModel.catalogSyncState.value.isRefreshing)

        repository.response.complete(true)
        runCurrent()
        val state = viewModel.uiState.value
        assertEquals("Remote", state.builds.single().title)
        assertEquals("endfield-endministrator", state.selectedCharacter?.id)
        assertEquals("End", state.characterQuery)
        assertEquals("Physical", state.selectedCharacterFilter)
        assertEquals(BuildType.F2P, state.selectedBuildType)
        assertEquals("2026-09-17", viewModel.catalogSyncState.value.publishedAt)
        assertFalse(viewModel.catalogSyncState.value.isRefreshing)
        assertFalse(viewModel.catalogSyncState.value.failed)
    }

    @Test
    fun responseAfterSwitchingGameDoesNotReplaceItsState() = runTest(dispatcher) {
        val repository = UpdatingRepository()
        val viewModel = GameBuildViewModel(repository)
        selectDetail(viewModel)
        viewModel.initializeCatalog()
        runCurrent()
        viewModel.selectGame(Game.WARFRAME)
        viewModel.selectCharacter(viewModel.uiState.value.characters.first())
        val state = viewModel.uiState.value

        repository.response.complete(true)
        runCurrent()
        assertEquals(state, viewModel.uiState.value)
    }

    @Test
    fun responseAfterReturningToGamesDoesNotReopenDetail() = runTest(dispatcher) {
        val repository = UpdatingRepository()
        val viewModel = GameBuildViewModel(repository)
        selectDetail(viewModel)
        viewModel.initializeCatalog()
        runCurrent()
        viewModel.backToGames()

        repository.response.complete(true)
        runCurrent()
        assertEquals(GameBuildUiState(), viewModel.uiState.value)
    }

    @Test
    fun responseUsesTheCurrentlySelectedCharacter() = runTest(dispatcher) {
        val repository = UpdatingRepository()
        val viewModel = GameBuildViewModel(repository)
        selectDetail(viewModel)
        viewModel.initializeCatalog()
        runCurrent()
        viewModel.backToCharacters()
        val second = viewModel.uiState.value.characters[1]
        viewModel.selectCharacter(second)

        repository.response.complete(true)
        runCurrent()
        assertEquals(second, viewModel.uiState.value.selectedCharacter)
        assertEquals(second.id, viewModel.uiState.value.builds.single().characterId)
        assertEquals("Remote", viewModel.uiState.value.builds.single().title)
    }

    @Test
    fun avoidsOverlappingRequestsAndAllowsManualRetryAfterFailure() = runTest(dispatcher) {
        val repository = UpdatingRepository()
        val viewModel = GameBuildViewModel(repository)
        selectDetail(viewModel)
        viewModel.initializeCatalog()
        viewModel.initializeCatalog()
        viewModel.refreshEndfieldBuilds()
        runCurrent()
        assertEquals(1, repository.requests)

        repository.response.complete(false)
        runCurrent()
        assertEquals("Cached", viewModel.uiState.value.builds.single().title)
        assertTrue(viewModel.catalogSyncState.value.failed)
        assertFalse(viewModel.catalogSyncState.value.isRefreshing)

        viewModel.initializeCatalog()
        runCurrent()
        assertEquals(1, repository.requests)
        repository.response = CompletableDeferred(true)
        viewModel.refreshEndfieldBuilds()
        runCurrent()
        assertEquals(2, repository.requests)
        assertEquals("Remote", viewModel.uiState.value.builds.single().title)
        assertFalse(viewModel.catalogSyncState.value.failed)
    }

    private fun selectDetail(viewModel: GameBuildViewModel) {
        viewModel.selectGame(Game.ENDFIELD)
        viewModel.selectCharacter(viewModel.uiState.value.characters.first())
    }

    private class UpdatingRepository : GameRepository {
        private val local = LocalGameRepository()
        private var cached = false
        private var remote = false
        var response = CompletableDeferred<Boolean>()
        var requests = 0
        override val endfieldCatalogDate: String?
            get() = if (cached || remote) "2026-09-17" else null

        override fun getCharacters(game: Game) = local.getCharacters(game)

        override fun getBuilds(game: Game, characterId: String) =
            local.getBuilds(game, characterId).map {
                if (game == Game.ENDFIELD && (remote || cached)) {
                    it.copy(title = if (remote) "Remote" else "Cached")
                } else it
            }

        override suspend fun loadCachedEndfieldBuilds() { cached = true }

        override suspend fun refreshEndfieldBuilds(): Boolean {
            requests++
            return response.await().also { if (it) remote = true }
        }
    }
}
