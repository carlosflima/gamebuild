package com.carlosflima.gamebuild.domain

data class GameCharacter(
    val id: String,
    val name: String,
    val role: String,
    val game: Game,
    val rarity: String? = null,
    val element: String? = null,
    val arcType: String? = null,
    val tier: String? = null,
    val imageUrl: String? = null,
    val sourceUrl: String? = null
)

data class CharacterBuild(
    val characterId: String,
    val title: String,
    val version: String? = null,
    val arcRecommendation: String? = null,
    val alternativeArcs: List<String> = emptyList(),
    val cartridgeRecommendation: String? = null,
    val modulePriority: List<String> = emptyList(),
    val statPriority: List<String> = emptyList(),
    val skillPriority: List<String> = emptyList(),
    val teamRecommendation: List<String> = emptyList(),
    val f2pTeam: List<String> = emptyList(),
    val f2pNote: String? = null,
    val sources: List<String> = emptyList(),
    val sourceUrl: String? = null,
    val sourceUpdatedAt: String? = null
)
