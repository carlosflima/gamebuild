package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryTest {
    private val character = GameCharacter(
        id = "test-character",
        name = "Test Character",
        role = "Test Role",
        game = Game.NTE
    )

    private val build = CharacterBuild(
        id = "test-build",
        characterId = character.id,
        title = "Test Build",
        type = BuildType.META,
        version = "test",
        weapon = "test",
        equipment = emptyList(),
        statPriority = emptyList(),
        team = emptyList(),
        notes = "test"
    )

    private val source = object : LocalGameDataSource {
        override val game: Game = Game.NTE
        override val characters: List<GameCharacter> = listOf(character)

        override fun getBuilds(characterId: String): List<CharacterBuild> =
            if (characterId == character.id) listOf(build) else emptyList()
    }

    private val repository: GameRepository = LocalGameRepository(listOf(source))

    @Test
    fun `default repository wires the NTE source`() {
        assertEquals(
            NteLocalDataSource.characters,
            LocalGameRepository().getCharacters(Game.NTE)
        )
    }

    @Test
    fun `default future games stay empty while their catalogs are in preparation`() {
        val defaultRepository = LocalGameRepository()
        val nteCharacterId = NteLocalDataSource.characters.first().id

        listOf(Game.WARFRAME, Game.ENDFIELD).forEach { game ->
            assertTrue(defaultRepository.getCharacters(game).isEmpty())
            assertTrue(defaultRepository.getBuilds(game, nteCharacterId).isEmpty())
        }
    }

    @Test
    fun `registered game exposes source characters`() {
        assertEquals(listOf(character), repository.getCharacters(Game.NTE))
    }

    @Test
    fun `unregistered games expose no characters`() {
        assertTrue(repository.getCharacters(Game.WARFRAME).isEmpty())
        assertTrue(repository.getCharacters(Game.ENDFIELD).isEmpty())
    }

    @Test
    fun `registered game delegates build lookup`() {
        assertEquals(listOf(build), repository.getBuilds(Game.NTE, character.id))
    }

    @Test
    fun `unregistered game does not expose registered builds`() {
        assertTrue(repository.getBuilds(Game.WARFRAME, character.id).isEmpty())
        assertTrue(repository.getBuilds(Game.ENDFIELD, character.id).isEmpty())
    }

    @Test
    fun `unknown character returns no builds`() {
        assertTrue(repository.getBuilds(Game.NTE, "missing-character").isEmpty())
    }
}
