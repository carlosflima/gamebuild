package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryTest {
    private val repository: GameRepository = FakeGameRepository()

    @Test
    fun `NTE exposes the local character catalog`() {
        assertEquals(
            NteLocalDataSource.characters,
            repository.getCharacters(Game.NTE)
        )
    }

    @Test
    fun `unavailable games expose no characters`() {
        assertTrue(repository.getCharacters(Game.WARFRAME).isEmpty())
        assertTrue(repository.getCharacters(Game.ENDFIELD).isEmpty())
    }

    @Test
    fun `known character returns its builds`() {
        val builds = repository.getBuilds("nte-nanally")

        assertTrue(builds.isNotEmpty())
        assertTrue(builds.all { it.characterId == "nte-nanally" })
    }

    @Test
    fun `unknown character returns no builds`() {
        assertTrue(repository.getBuilds("missing-character").isEmpty())
    }
}
