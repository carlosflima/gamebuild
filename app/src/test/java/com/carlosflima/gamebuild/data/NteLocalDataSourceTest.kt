package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NteLocalDataSourceTest {

    @Test
    fun `characters have unique ids and required fields`() {
        val characters = NteLocalDataSource.characters

        assertTrue(characters.isNotEmpty())
        assertEquals(characters.size, characters.map { it.id }.distinct().size)

        characters.forEach { character ->
            assertTrue(character.id.isNotBlank())
            assertTrue(character.name.isNotBlank())
            assertTrue(character.role.isNotBlank())
            assertEquals(Game.NTE, character.game)
            character.imageUrl?.let { assertTrue(it.isNotBlank()) }
        }
    }

    @Test
    fun `builds reference known characters and have unique ids`() {
        val characters = NteLocalDataSource.characters
        val characterIds = characters.map { it.id }.toSet()
        val builds = NteBuildCatalog.builds
        val indexedBuilds = characters.flatMap { character ->
            NteLocalDataSource.getBuilds(character.id).onEach { build ->
                assertEquals(character.id, build.characterId)
            }
        }

        assertTrue(builds.isNotEmpty())
        assertEquals(builds.size, builds.map { it.id }.distinct().size)
        assertTrue(builds.all { build -> build.characterId in characterIds })
        assertEquals(builds.map { it.id }.toSet(), indexedBuilds.map { it.id }.toSet())
    }

    @Test
    fun `builds keep essential content structurally complete`() {
        val builds = NteBuildCatalog.builds

        builds.forEach { build ->
            assertTrue(build.id.isNotBlank())
            assertTrue(build.title.isNotBlank())
            assertTrue(build.version.isNotBlank())
            assertTrue(build.weapon.isNotBlank())
            assertTrue(build.equipment.isNotEmpty())
            assertTrue(build.equipment.all { it.isNotBlank() })
            assertTrue(build.equipmentImageUrls.size <= build.equipment.size)
            assertTrue(build.statPriority.isNotEmpty())
            assertTrue(build.statPriority.all { it.isNotBlank() })
            assertTrue(build.team.isNotEmpty())
            assertTrue(build.team.all { it.isNotBlank() })
            assertTrue(build.notes.isNotBlank())
            assertTrue(build.sources.isNotEmpty())
            assertTrue(build.sources.all { source ->
                source.name.isNotBlank() && (source.url == null || source.url.isNotBlank())
            })
        }
    }
}
