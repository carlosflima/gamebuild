package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LocalGameDataSourceTest {

    @Test
    fun `map indexes source by declared game`() {
        val dataSources = localGameDataSourceMapOf(NteLocalDataSource)

        assertEquals(1, dataSources.size)
        assertSame(NteLocalDataSource, dataSources[Game.NTE])
    }

    @Test(expected = IllegalArgumentException::class)
    fun `map rejects duplicate sources for the same game`() {
        localGameDataSourceMapOf(
            stubDataSource(Game.NTE),
            stubDataSource(Game.NTE)
        )
    }

    private fun stubDataSource(game: Game): LocalGameDataSource = object : LocalGameDataSource {
        override val game: Game = game
        override val characters: List<GameCharacter> = emptyList()

        override fun getBuilds(characterId: String): List<CharacterBuild> = emptyList()
    }
}
