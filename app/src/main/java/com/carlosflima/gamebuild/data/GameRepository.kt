package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuilds(game: Game, characterId: String): List<CharacterBuild>
}

class LocalGameRepository : GameRepository {
    private val dataSources: Map<Game, LocalGameDataSource> = localGameDataSourceMapOf(
        NteLocalDataSource
    )

    override fun getCharacters(game: Game): List<GameCharacter> =
        dataSources[game]?.characters.orEmpty()

    override fun getBuilds(game: Game, characterId: String): List<CharacterBuild> =
        dataSources[game]?.getBuilds(characterId).orEmpty()
}
