package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildType
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
    fun `every starter Warframe has at least one build`() {
        WarframeLocalDataSource.characters.forEach { character ->
            assertTrue(
                "Expected at least one build for ${character.id}",
                WarframeLocalDataSource.getBuilds(character.id).isNotEmpty()
            )
        }
    }

    @Test
    fun `starter builds reference known characters and have unique ids`() {
        val characterIds = WarframeLocalDataSource.characters.map { it.id }.toSet()
        val builds = WarframeBuildCatalog.builds
        val indexedBuilds = WarframeLocalDataSource.characters.flatMap { character ->
            WarframeLocalDataSource.getBuilds(character.id).onEach { build ->
                assertEquals(character.id, build.characterId)
            }
        }

        assertEquals(3, builds.size)
        assertEquals(builds.size, builds.map { it.id }.distinct().size)
        assertTrue(builds.all { it.characterId in characterIds })
        assertEquals(builds.map { it.id }.toSet(), indexedBuilds.map { it.id }.toSet())
    }

    @Test
    fun `starter builds are F2P update 43 5 snapshots with sources`() {
        WarframeBuildCatalog.builds.forEach { build ->
            assertEquals(BuildType.F2P, build.type)
            assertEquals("Update 43.5", build.version)
            assertTrue(build.weapon.isNotBlank())
            assertTrue(build.equipment.isNotEmpty())
            assertTrue(build.equipment.all { it.isNotBlank() })
            assertTrue(build.statPriority.isNotEmpty())
            assertTrue(build.notes.isNotBlank())
            assertTrue(build.sources.size >= 2)
            assertTrue(build.sources.all { source ->
                source.name.isNotBlank() && !source.url.isNullOrBlank()
            })
        }
    }
}
