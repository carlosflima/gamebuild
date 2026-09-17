package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.time.LocalDate

internal data class EndfieldCatalogDocument(
    val revision: Int,
    val publishedAt: String,
    val builds: Map<String, CharacterBuild>
)

internal object EndfieldCatalogParser {
    const val MAX_DOCUMENT_BYTES = 262_144
    const val GAME_VERSION = "Dreamscape of Wind and Snow · 2026-09"

    private val localBuilds = EndfieldLocalDataSource.characters.associate { character ->
        character.id to EndfieldLocalDataSource.getBuilds(character.id).single()
    }

    fun parse(json: String): EndfieldCatalogDocument? = runCatching {
        require(json.toByteArray(Charsets.UTF_8).size in 1..MAX_DOCUMENT_BYTES)
        val root = JSONObject(json)
        root.requireKeys("schemaVersion", "revision", "publishedAt", "gameVersion", "builds")
        require(root.opt("schemaVersion") == 1)
        val revision = root.opt("revision")
        require(revision is Int && revision > 0)
        val publishedAt = root.text("publishedAt", 10)
        require(LocalDate.parse(publishedAt).toString() == publishedAt)
        require(root.text("gameVersion", 100) == GAME_VERSION)

        val entries = root.getJSONArray("builds")
        require(entries.length() == localBuilds.size)
        val builds = LinkedHashMap<String, CharacterBuild>()
        for (index in 0 until entries.length()) {
            val entry = entries.getJSONObject(index)
            entry.requireKeys(
                "id", "characterId", "title", "type", "weapon",
                "equipment", "statPriority", "team", "notes", "sources"
            )
            val characterId = entry.text("characterId", 100)
            val local = requireNotNull(localBuilds[characterId])
            require(entry.text("id", 100) == local.id)
            require(entry.text("type", 10) == BuildType.F2P.name)
            require(entry.getJSONArray("team").length() == 0)
            val sources = entry.getJSONArray("sources")
            require(sources.length() in 1..8)
            val parsedSources = (0 until sources.length()).map { sourceIndex ->
                val source = sources.getJSONObject(sourceIndex)
                source.requireKeys("name", "url")
                val url = source.text("url", 2_048)
                val uri = URI(url)
                require(uri.scheme == "https" && !uri.host.isNullOrBlank() && uri.userInfo == null)
                BuildSource(source.text("name", 160), url)
            }
            val build = local.copy(
                title = entry.text("title", 160),
                weapon = entry.text("weapon", 160),
                weaponImageUrl = null,
                equipment = entry.getJSONArray("equipment").texts(4..4),
                equipmentImageUrls = emptyList(),
                statPriority = entry.getJSONArray("statPriority").texts(1..8),
                team = emptyList(),
                notes = entry.text("notes", 4_000),
                sources = parsedSources
            )
            require(builds.put(characterId, build) == null)
        }
        require(builds.keys == localBuilds.keys)
        EndfieldCatalogDocument(revision, publishedAt, builds.toMap())
    }.getOrNull()

    private fun JSONObject.requireKeys(vararg expected: String) {
        require(keys().asSequence().toSet() == expected.toSet())
    }

    private fun JSONObject.text(key: String, maxLength: Int): String =
        checkedText(get(key), maxLength)

    private fun JSONArray.texts(count: IntRange): List<String> {
        require(length() in count)
        return (0 until length()).map { checkedText(get(it), 160) }
    }

    private fun checkedText(value: Any, maxLength: Int): String {
        require(value is String && value.isNotBlank() && value.length <= maxLength)
        require(value.none { Character.isISOControl(it) })
        return value
    }
}
