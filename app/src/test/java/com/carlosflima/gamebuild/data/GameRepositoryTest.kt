package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GameRepositoryTest {
    private val repository: GameRepository = LocalGameRepository()

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
    fun `known NTE character returns its builds`() {
        val builds = repository.getBuilds(Game.NTE, "nte-nanally")

        assertTrue(builds.isNotEmpty())
        assertTrue(builds.all { it.characterId == "nte-nanally" })
    }

    @Test
    fun `unsupported game does not expose NTE builds`() {
        assertTrue(repository.getBuilds(Game.WARFRAME, "nte-nanally").isEmpty())
        assertTrue(repository.getBuilds(Game.ENDFIELD, "nte-nanally").isEmpty())
    }

    @Test
    fun `unknown NTE character returns no builds`() {
        assertTrue(repository.getBuilds(Game.NTE, "missing-character").isEmpty())
    }
}
