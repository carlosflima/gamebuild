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
    fun `map indexes all default sources by declared game`() {
        val dataSources = localGameDataSourceMapOf(
            NteLocalDataSource,
            WarframeLocalDataSource,
            EndfieldLocalDataSource
        )

        assertEquals(Game.entries.size, dataSources.size)
        assertSame(NteLocalDataSource, dataSources[Game.NTE])
        assertSame(WarframeLocalDataSource, dataSources[Game.WARFRAME])
        assertSame(EndfieldLocalDataSource, dataSources[Game.ENDFIELD])
    }

    @Test
    fun `future game sources start registered but empty`() {
        listOf(WarframeLocalDataSource, EndfieldLocalDataSource).forEach { dataSource ->
            assertTrue(dataSource.characters.isEmpty())
            assertTrue(dataSource.getBuilds("missing-character").isEmpty())
        }
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
