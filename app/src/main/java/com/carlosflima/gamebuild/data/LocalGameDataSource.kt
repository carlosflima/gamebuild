package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal interface LocalGameDataSource {
    val game: Game
    val characters: List<GameCharacter>

    fun getBuilds(characterId: String): List<CharacterBuild>
}

internal fun localGameDataSourceMapOf(
    vararg dataSources: LocalGameDataSource
): Map<Game, LocalGameDataSource> {
    val duplicateGames = dataSources
        .groupBy { it.game }
        .filterValues { it.size > 1 }
        .keys

    require(duplicateGames.isEmpty()) {
        "Duplicate local game data sources: ${duplicateGames.joinToString { it.name }}"
    }

    return dataSources.associateBy { it.game }
}
