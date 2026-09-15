package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GameBuildGameSwitchTest {

    private val nteCharacter = GameCharacter(
        id = "nte-nanally",
        name = "Nanally",
        role = "Damage · Anima",
        game = Game.NTE
    )
    private val warframeCharacter = GameCharacter(
        id = "warframe-excalibur",
        name = "Excalibur",
        role = "Starter · Physical",
        game = Game.WARFRAME
    )
    private val nteBuilds = listOf(
        build("nte-meta", nteCharacter.id, BuildType.META),
        build("nte-f2p", nteCharacter.id, BuildType.F2P)
    )

    private val repository = object : GameRepository {
        override fun getCharacters(game: Game): List<GameCharacter> = when (game) {
            Game.NTE -> listOf(nteCharacter)
            Game.WARFRAME -> listOf(warframeCharacter)
            Game.ENDFIELD -> emptyList()
        }

        override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> = when (game) {
            Game.NTE -> nteBuilds
            Game.WARFRAME, Game.ENDFIELD -> emptyList()
        }
    }

    @Test
    fun `switching games clears roster filters and build selection`() {
        val viewModel = GameBuildViewModel(repository)
        viewModel.selectGame(Game.NTE)
        viewModel.updateCharacterQuery("nan")
        viewModel.toggleCharacterFilter("Anima")
        viewModel.selectCharacter(nteCharacter)
        viewModel.selectBuildType(BuildType.F2P)

        viewModel.selectGame(Game.WARFRAME)

        val state = viewModel.uiState.value
        assertEquals(Game.WARFRAME, state.selectedGame)
        assertEquals(listOf(warframeCharacter), state.characters)
        assertEquals("", state.characterQuery)
        assertNull(state.selectedCharacterFilter)
        assertNull(state.selectedCharacter)
        assertEquals(emptyList<CharacterBuild>(), state.builds)
        assertNull(state.selectedBuildType)
        assertNull(state.errorMessage)
        assertEquals(listOf(warframeCharacter), state.filteredCharacters)
    }

    private fun build(id: String, characterId: String, type: BuildType) = CharacterBuild(
        id = id,
        characterId = characterId,
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
