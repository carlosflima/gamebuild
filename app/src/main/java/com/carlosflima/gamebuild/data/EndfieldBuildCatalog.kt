package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild

internal object EndfieldBuildCatalog {
    internal val builds: List<CharacterBuild> = emptyList()

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> =
        builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> =
        buildsByCharacterId[characterId].orEmpty()
}
