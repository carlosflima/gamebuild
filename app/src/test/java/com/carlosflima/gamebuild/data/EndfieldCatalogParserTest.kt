package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildType
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EndfieldCatalogParserTest {
    @Test
    fun publishedDocumentCoversExactlyTheBundledRoster() {
        val document = requireNotNull(EndfieldCatalogParser.parse(endfieldCatalogJson()))
        val characters = EndfieldLocalDataSource.characters
        assertEquals(characters.map { it.id }.toSet(), document.builds.keys)
        characters.forEach { character ->
            val build = requireNotNull(document.builds[character.id])
            assertEquals(EndfieldLocalDataSource.getBuilds(character.id).single().id, build.id)
            assertEquals(EndfieldCatalogParser.GAME_VERSION, build.version)
            assertEquals(BuildType.F2P, build.type)
            assertTrue(build.team.isEmpty())
            assertTrue(build.sources.isNotEmpty())
        }
    }

    @Test
    fun acceptsContentChangesWithoutChangingBuildIdentity() {
        val document = requireNotNull(EndfieldCatalogParser.parse(changedEndfieldCatalog(2)))
        val build = document.builds.getValue("endfield-endministrator")
        assertEquals("Updated starter", build.title)
        assertEquals("endfield-endministrator-f2p-starter-2026-09", build.id)
    }

    @Test
    fun rejectsUnsupportedSchemaVersionSnapshotAndMetadata() {
        listOf(
            "schemaVersion" to 2,
            "schemaVersion" to "1",
            "revision" to 0,
            "revision" to 1.5,
            "revision" to "2",
            "publishedAt" to "2026-02-30",
            "gameVersion" to "Different patch",
            "extra" to true
        ).forEach { (key, value) -> reject { it.put(key, value) } }
    }

    @Test
    fun rejectsMissingDuplicateAndUnknownCharacters() {
        reject { it.getJSONArray("builds").remove(0) }
        reject {
            val builds = it.getJSONArray("builds")
            builds.put(1, builds.getJSONObject(0))
        }
        rejectBuild { it.put("characterId", "nte-character") }
        rejectBuild { it.put("id", "unknown-build") }
    }

    @Test
    fun rejectsWrongTypesNonF2pTeamsAndIncompleteEquipment() {
        rejectBuild { it.put("type", "META") }
        rejectBuild { it.put("team", JSONArray().put("operator")) }
        rejectBuild { it.put("equipment", JSONArray().put("single piece")) }
        rejectBuild { it.put("weapon", 7) }
        rejectBuild { it.put("statPriority", JSONArray()) }
        rejectBuild { it.remove("notes") }
        rejectBuild { it.put("title", "") }
        rejectBuild { it.put("title", "a".repeat(161)) }
        rejectBuild { it.put("notes", "a".repeat(4_001)) }
        rejectBuild { it.put("title", "bad\u0000title") }
    }

    @Test
    fun rejectsUnsafeOrMissingReferencesAndOversizedDocuments() {
        rejectBuild { it.put("sources", JSONArray()) }
        listOf("http://example.com", "javascript:alert(1)", "https://user@example.com", "https:///missing")
            .forEach { url ->
                rejectBuild { it.getJSONArray("sources").getJSONObject(0).put("url", url) }
            }
        assertNull(EndfieldCatalogParser.parse("x".repeat(EndfieldCatalogParser.MAX_DOCUMENT_BYTES + 1)))
        assertNull(EndfieldCatalogParser.parse("{"))
    }

    @Test
    fun acceptsRepeatedEquipmentSlotsAndHttpsReferences() {
        val root = JSONObject(endfieldCatalogJson())
        root.getJSONArray("builds").getJSONObject(0).put(
            "equipment", JSONArray(listOf("Armor", "Gloves", "Plate", "Plate"))
        )
        assertNotNull(EndfieldCatalogParser.parse(root.toString()))
    }

    private fun reject(change: (JSONObject) -> Unit) {
        val root = JSONObject(endfieldCatalogJson())
        change(root)
        assertNull(EndfieldCatalogParser.parse(root.toString()))
    }

    private fun rejectBuild(change: (JSONObject) -> Unit) =
        reject { change(it.getJSONArray("builds").getJSONObject(0)) }
}
