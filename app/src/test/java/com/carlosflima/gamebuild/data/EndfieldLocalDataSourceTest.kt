package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildType
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
    fun `every starter operator has exactly one build`() {
        EndfieldLocalDataSource.characters.forEach { character ->
            assertEquals(
                "Expected one starter build for ${character.id}",
                1,
                EndfieldLocalDataSource.getBuilds(character.id).size
            )
        }
    }

    @Test
    fun `starter builds reference known characters and have unique ids`() {
        val characterIds = EndfieldLocalDataSource.characters.map { it.id }.toSet()
        val builds = EndfieldBuildCatalog.builds
        val indexedBuilds = EndfieldLocalDataSource.characters.flatMap { character ->
            EndfieldLocalDataSource.getBuilds(character.id).onEach { build ->
                assertEquals(character.id, build.characterId)
            }
        }

        assertEquals(4, builds.size)
        assertEquals(builds.size, builds.map { it.id }.distinct().size)
        assertTrue(builds.all { it.characterId in characterIds })
        assertEquals(builds.map { it.id }.toSet(), indexedBuilds.map { it.id }.toSet())
    }

    @Test
    fun `starter builds are F2P current-version snapshots with sources`() {
        EndfieldBuildCatalog.builds.forEach { build ->
            assertEquals(BuildType.F2P, build.type)
            assertEquals("Dreamscape of Wind and Snow · 2026-09", build.version)
            assertTrue(build.weapon.isNotBlank())
            assertEquals(4, build.equipment.size)
            assertTrue(build.equipment.all { it.isNotBlank() })
            assertTrue(build.statPriority.isNotEmpty())
            assertTrue(build.team.isEmpty())
            assertTrue(build.notes.isNotBlank())
            assertTrue(build.sources.size >= 2)
            assertTrue(build.sources.all { source ->
                source.name.isNotBlank() && !source.url.isNullOrBlank()
            })
        }
    }
}
