package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Test

class GameBuildViewModelTest {
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
    fun filtersCharactersByNameAndRoleAndRestoresFullRoster() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)

        viewModel.updateCharacterQuery("nan")
        assertEquals(listOf("Nanally"), viewModel.uiState.value.filteredCharacters.map { it.name })

        viewModel.updateCharacterQuery("incantation")
        assertEquals(listOf("Sakiri"), viewModel.uiState.value.filteredCharacters.map { it.name })

        viewModel.updateCharacterQuery("")
        assertEquals(characters, viewModel.uiState.value.filteredCharacters)
    }

    @Test
    fun unavailableGameDoesNotNavigateToCharacterList() {
        val viewModel = GameBuildViewModel(repository)

        viewModel.selectGame(Game.WARFRAME)

        assertEquals(null, viewModel.uiState.value.selectedGame)
        assertEquals(emptyList<GameCharacter>(), viewModel.uiState.value.characters)
        assertEquals(
            "Conteúdo de Warframe ainda está em preparação.",
            viewModel.uiState.value.errorMessage
        )
    }

    @Test
    fun filtersBuildsByTypeAndRestoresAllBuilds() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)
        viewModel.selectCharacter(characters.first())

        assertEquals(listOf(BuildType.META, BuildType.F2P), viewModel.uiState.value.availableBuildTypes)
        assertEquals(builds, viewModel.uiState.value.filteredBuilds)

        viewModel.selectBuildType(BuildType.F2P)
        assertEquals(listOf("f2p"), viewModel.uiState.value.filteredBuilds.map { it.id })

        viewModel.selectBuildType(null)
        assertEquals(builds, viewModel.uiState.value.filteredBuilds)
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
