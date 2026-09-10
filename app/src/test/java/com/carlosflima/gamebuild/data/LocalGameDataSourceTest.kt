package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalGameDataSourceTest {

    @Test
    fun `default source list covers every game by declared identity`() {
        val dataSources = localGameDataSourceMapOf(*defaultLocalGameDataSources.toTypedArray())

        assertEquals(Game.entries.size, dataSources.size)
        assertSame(NteLocalDataSource, dataSources[Game.NTE])
        assertSame(WarframeLocalDataSource, dataSources[Game.WARFRAME])
        assertSame(EndfieldLocalDataSource, dataSources[Game.ENDFIELD])
    }

    @Test
    fun `Endfield source stays empty while its catalog has not started`() {
        assertTrue(EndfieldLocalDataSource.characters.isEmpty())
        assertTrue(EndfieldLocalDataSource.getBuilds("missing-character").isEmpty())
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
