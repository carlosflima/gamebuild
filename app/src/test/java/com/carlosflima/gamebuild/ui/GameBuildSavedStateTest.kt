package com.carlosflima.gamebuild.ui

import androidx.lifecycle.SavedStateHandle
import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GameBuildSavedStateTest {
    private val operator = GameCharacter("operator", "Operator", "Guard · Heat", Game.ENDFIELD)
    private val other = GameCharacter("other", "Other", "Support", Game.WARFRAME)
    private val builds = listOf(build(BuildType.META), build(BuildType.F2P))
    private val handle = SavedStateHandle()
    private val repository = repository()
    private val viewModel = GameBuildViewModel(repository, handle)

    @Test
    fun restoresDetailAndFiltersFromLightweightState() {
        selectDetail()

        val saved = snapshot()
        val restored = GameBuildViewModel(repository, SavedStateHandle(saved)).uiState.value

        assertEquals(viewModel.uiState.value, restored)
        assertEquals(listOf(builds.last()), restored.filteredBuilds)
        assertTrue(saved.values.all { it == null || it is String })
    }

    @Test
    fun returningToRosterPreservesSearchButDoesNotReopenDetail() {
        selectDetail()
        viewModel.backToCharacters()

        val restored = recreate()
        assertEquals(Game.ENDFIELD, restored.selectedGame)
        assertEquals("Oper", restored.characterQuery)
        assertEquals("Heat", restored.selectedCharacterFilter)
        assertNull(restored.selectedCharacter)
        assertTrue(restored.builds.isEmpty())
        assertNull(restored.selectedBuildType)
    }

    @Test
    fun returningToGamesClearsSavedNavigation() {
        selectDetail()
        viewModel.backToGames()

        assertEquals(GameBuildUiState(), recreate())
    }

    @Test
    fun switchingGamesDoesNotRestoreOldSelections() {
        selectDetail()
        viewModel.selectGame(Game.WARFRAME)

        assertEquals(
            GameBuildUiState(selectedGame = Game.WARFRAME, characters = listOf(other)),
            recreate()
        )
    }

    @Test
    fun removedCharacterAndFilterFallBackToCurrentRoster() {
        selectDetail()
        val replacement = operator.copy(id = "replacement", role = "Caster · Cryo")
        val changedRepository = repository(listOf(replacement, other))

        val restoredHandle = SavedStateHandle(snapshot())
        val restored = GameBuildViewModel(changedRepository, restoredHandle).uiState.value

        assertEquals(listOf(replacement), restored.characters)
        assertEquals("Oper", restored.characterQuery)
        assertNull(restored.selectedCharacterFilter)
        assertNull(restored.selectedCharacter)
        assertNull(restored.selectedBuildType)
        assertTrue(restored.builds.isEmpty())
        assertNull(GameBuildViewModel(repository, restoredHandle).uiState.value.selectedCharacter)
    }

    @Test
    fun reloadsCurrentBuildsAndDropsUnavailableBuildType() {
        selectDetail()
        val updatedBuild = builds.first().copy(title = "Updated catalog")
        val restored = recreate(repository(currentBuilds = listOf(updatedBuild)))

        assertEquals(operator, restored.selectedCharacter)
        assertEquals(listOf(updatedBuild), restored.builds)
        assertNull(restored.selectedBuildType)
        assertEquals(listOf(updatedBuild), restored.filteredBuilds)
    }

    @Test
    fun invalidSavedGameOrValueTypesUseSafeDefaults() {
        val unknownGame = SavedStateHandle(mapOf("selectedGame" to "REMOVED"))
        assertEquals(GameBuildUiState(), GameBuildViewModel(repository, unknownGame).uiState.value)

        val wrongTypes = SavedStateHandle(
            mapOf(
                "selectedGame" to Game.ENDFIELD.name,
                "characterQuery" to 7,
                "characterFilter" to true,
                "selectedCharacter" to 9,
                "buildType" to 11
            )
        )
        assertEquals(
            GameBuildUiState(selectedGame = Game.ENDFIELD, characters = listOf(operator)),
            GameBuildViewModel(repository, wrongTypes).uiState.value
        )
    }

    @Test
    fun buildLoadFailureRestoresRosterAndCanBeDismissed() {
        selectDetail()
        val failingRepository = object : GameRepository {
            override fun getCharacters(game: Game) = repository.getCharacters(game)
            override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> =
                error("builds unavailable")
        }
        val restored = GameBuildViewModel(failingRepository, SavedStateHandle(snapshot()))

        assertEquals(Game.ENDFIELD, restored.uiState.value.selectedGame)
        assertNull(restored.uiState.value.selectedCharacter)
        assertTrue(restored.uiState.value.builds.isEmpty())
        assertEquals("builds unavailable", restored.uiState.value.errorMessage)
        restored.clearError()
        assertNull(restored.uiState.value.errorMessage)
    }

    @Test
    fun rosterLoadFailureLeavesNavigationAtGames() {
        selectDetail()
        val failingRepository = object : GameRepository {
            override fun getCharacters(game: Game): List<GameCharacter> = error("roster unavailable")
            override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> = emptyList()
        }

        val restored = recreate(failingRepository)

        assertEquals(GameBuildUiState(errorMessage = "roster unavailable"), restored)
    }

    private fun selectDetail() {
        viewModel.selectGame(Game.ENDFIELD)
        viewModel.updateCharacterQuery("Oper")
        viewModel.toggleCharacterFilter("Heat")
        viewModel.selectCharacter(operator)
        viewModel.selectBuildType(BuildType.F2P)
    }

    private fun snapshot(): Map<String, Any?> =
        handle.keys().associateWith { handle.get<Any?>(it) }

    private fun recreate(source: GameRepository = repository): GameBuildUiState =
        GameBuildViewModel(source, SavedStateHandle(snapshot())).uiState.value

    private fun repository(
        roster: List<GameCharacter> = listOf(operator, other),
        currentBuilds: List<CharacterBuild> = builds
    ) = object : GameRepository {
        override fun getCharacters(game: Game) = roster.filter { it.game == game }
        override fun getBuilds(game: Game, characterId: String) =
            currentBuilds.filter { it.characterId == characterId }
    }

    private fun build(type: BuildType) = CharacterBuild(
        id = type.name,
        characterId = operator.id,
        title = type.name,
        type = type,
        version = "test",
        weapon = "test",
        equipment = emptyList(),
        statPriority = emptyList(),
        team = emptyList(),
        notes = "test"
    )
}
