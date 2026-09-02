package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameBuildNavigationStateTest {
    private val characters = listOf(
        GameCharacter("1", "Nanally", "Damage · Anima", Game.NTE),
        GameCharacter("2", "Sakiri", "Buff · Incantation", Game.NTE),
        GameCharacter("3", "Zero", "Damage / Cycle enabler · Cosmos", Game.NTE)
    )

    private val builds = listOf(
        build("meta", BuildType.META),
        build("f2p", BuildType.F2P)
    )

    private val repository = object : GameRepository {
        override fun getCharacters(game: Game): List<GameCharacter> = characters
        override fun getBuilds(characterId: String): List<CharacterBuild> = builds
    }

    @Test
    fun togglesAndClearsCharacterFilters() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)

        viewModel.toggleCharacterFilter("Anima")
        assertEquals("Anima", viewModel.uiState.value.selectedCharacterFilter)
        assertEquals(listOf("Nanally"), viewModel.uiState.value.filteredCharacters.map { it.name })

        viewModel.toggleCharacterFilter("Anima")
        assertNull(viewModel.uiState.value.selectedCharacterFilter)
        assertEquals(characters, viewModel.uiState.value.filteredCharacters)

        viewModel.updateCharacterQuery("sak")
        viewModel.toggleCharacterFilter("Incantation")
        viewModel.clearCharacterFilters()

        assertEquals("", viewModel.uiState.value.characterQuery)
        assertNull(viewModel.uiState.value.selectedCharacterFilter)
        assertEquals(characters, viewModel.uiState.value.filteredCharacters)
    }

    @Test
    fun backToCharactersKeepsRosterFiltersAndClearsBuildState() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)
        viewModel.updateCharacterQuery("nan")
        viewModel.toggleCharacterFilter("Anima")
        viewModel.selectCharacter(characters.first())
        viewModel.selectBuildType(BuildType.F2P)

        viewModel.backToCharacters()

        val state = viewModel.uiState.value
        assertEquals(Game.NTE, state.selectedGame)
        assertEquals(characters, state.characters)
        assertEquals("nan", state.characterQuery)
        assertEquals("Anima", state.selectedCharacterFilter)
        assertNull(state.selectedCharacter)
        assertEquals(emptyList<CharacterBuild>(), state.builds)
        assertNull(state.selectedBuildType)
        assertNull(state.errorMessage)
    }

    @Test
    fun backToGamesResetsTheWholeUiState() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)
        viewModel.updateCharacterQuery("nan")
        viewModel.selectCharacter(characters.first())

        viewModel.backToGames()

        assertEquals(GameBuildUiState(), viewModel.uiState.value)
    }

    @Test
    fun repositoryErrorsAreExposedAndCanBeCleared() {
        val characterFailureRepository = object : GameRepository {
            override fun getCharacters(game: Game): List<GameCharacter> = error("roster unavailable")
            override fun getBuilds(characterId: String): List<CharacterBuild> = emptyList()
        }
        val rosterViewModel = GameBuildViewModel(characterFailureRepository)

        rosterViewModel.selectGame(Game.NTE)
        assertEquals("roster unavailable", rosterViewModel.uiState.value.errorMessage)
        assertNull(rosterViewModel.uiState.value.selectedGame)

        rosterViewModel.clearError()
        assertNull(rosterViewModel.uiState.value.errorMessage)

        val buildFailureRepository = object : GameRepository {
            override fun getCharacters(game: Game): List<GameCharacter> = characters
            override fun getBuilds(characterId: String): List<CharacterBuild> = error("builds unavailable")
        }
        val buildViewModel = GameBuildViewModel(buildFailureRepository)
        buildViewModel.selectGame(Game.NTE)

        buildViewModel.selectCharacter(characters.first())

        assertEquals("builds unavailable", buildViewModel.uiState.value.errorMessage)
        assertNull(buildViewModel.uiState.value.selectedCharacter)
        assertEquals(emptyList<CharacterBuild>(), buildViewModel.uiState.value.builds)
    }

    private fun build(id: String, type: BuildType) = CharacterBuild(
        id = id,
        characterId = characters.first().id,
        title = id,
        type = type,
        version = "test",
        weapon = "test",
        equipment = emptyList(),
        statPriority = emptyList(),
        team = emptyList(),
        notes = "test"
    )
}
