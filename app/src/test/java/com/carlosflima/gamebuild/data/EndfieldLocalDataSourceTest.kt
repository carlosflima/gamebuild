package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EndfieldLocalDataSourceTest {

    @Test
    fun `starter roster contains Endministrator Perlica Chen Qianyu and Wulfgard`() {
        assertEquals(
            listOf("Endministrator", "Perlica", "Chen Qianyu", "Wulfgard"),
            EndfieldLocalDataSource.characters.map { it.name }
        )
    }

    @Test
    fun `characters have unique ids and required fields`() {
        val characters = EndfieldLocalDataSource.characters

        assertEquals(characters.size, characters.map { it.id }.distinct().size)
        characters.forEach { character ->
            assertTrue(character.id.startsWith("endfield-"))
            assertTrue(character.name.isNotBlank())
            assertTrue(character.role.isNotBlank())
            assertEquals(Game.ENDFIELD, character.game)
        }
    }

    @Test
    fun `starter roster keeps builds empty while Endfield is in preparation`() {
        EndfieldLocalDataSource.characters.forEach { character ->
            assertTrue(EndfieldLocalDataSource.getBuilds(character.id).isEmpty())
        }
        assertTrue(EndfieldBuildCatalog.builds.isEmpty())
    }
}
