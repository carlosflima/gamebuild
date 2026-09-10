package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuilds(game: Game, characterId: String): List<CharacterBuild>
}

internal val defaultLocalGameDataSources: List<LocalGameDataSource> = listOf(
    NteLocalDataSource,
    WarframeLocalDataSource,
    EndfieldLocalDataSource
)

class LocalGameRepository internal constructor(
    sources: List<LocalGameDataSource>
) : GameRepository {
    constructor() : this(defaultLocalGameDataSources)

    private val dataSources: Map<Game, LocalGameDataSource> =
        localGameDataSourceMapOf(*sources.toTypedArray())

    override fun getCharacters(game: Game): List<GameCharacter> =
        dataSources[game]?.characters.orEmpty()

    override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> =
        dataSources[game]?.getBuilds(characterId).orEmpty()
}
