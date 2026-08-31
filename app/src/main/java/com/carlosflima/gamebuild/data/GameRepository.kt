package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuilds(characterId: String): List<CharacterBuild>
}

class FakeGameRepository : GameRepository {
    override fun getCharacters(game: Game): List<GameCharacter> = when (game) {
        Game.NTE -> NteLocalDataSource.characters
        Game.WARFRAME, Game.ENDFIELD -> emptyList()
    }

    override fun getBuilds(characterId: String): List<CharacterBuild> =
        NteLocalDataSource.getBuilds(characterId)
}
