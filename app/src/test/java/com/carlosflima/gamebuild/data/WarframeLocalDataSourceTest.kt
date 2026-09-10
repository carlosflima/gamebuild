package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WarframeLocalDataSourceTest {

    @Test
    fun `starter roster contains Excalibur Mag and Volt`() {
        assertEquals(
            listOf("Excalibur", "Mag", "Volt"),
            WarframeLocalDataSource.characters.map { it.name }
        )
    }

    @Test
    fun `characters have unique ids and required fields`() {
        val characters = WarframeLocalDataSource.characters

        assertEquals(characters.size, characters.map { it.id }.distinct().size)
        characters.forEach { character ->
            assertTrue(character.id.startsWith("warframe-"))
            assertTrue(character.name.isNotBlank())
            assertTrue(character.role.isNotBlank())
            assertEquals(Game.WARFRAME, character.game)
        }
    }

    @Test
    fun `starter roster intentionally has no builds yet`() {
        assertTrue(WarframeBuildCatalog.builds.isEmpty())
        WarframeLocalDataSource.characters.forEach { character ->
            assertTrue(WarframeLocalDataSource.getBuilds(character.id).isEmpty())
        }
    }
}
