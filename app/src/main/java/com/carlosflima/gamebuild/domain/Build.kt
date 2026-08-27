package com.carlosflima.gamebuild.domain

enum class BuildType(val displayName: String) {
    META("Meta"),
    F2P("F2P")
}

data class BuildSource(
    val name: String,
    val url: String? = null
)

data class CharacterBuild(
    val id: String,
    val characterId: String,
    val title: String,
    val type: BuildType,
    val version: String,
    val weapon: String,
    val equipment: List<String>,
    val statPriority: List<String>,
    val team: List<String>,
    val notes: String,
    val sources: List<BuildSource> = emptyList()
)
