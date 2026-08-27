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
        Game.WARFRAME -> listOf(
            GameCharacter("wf-demo-1", "Warframe Demo 01", "DPS", Game.WARFRAME),
            GameCharacter("wf-demo-2", "Warframe Demo 02", "Suporte", Game.WARFRAME)
        )
        Game.ENDFIELD -> listOf(
            GameCharacter("ef-demo-1", "Operador Demo 01", "DPS", Game.ENDFIELD),
            GameCharacter("ef-demo-2", "Operador Demo 02", "Suporte", Game.ENDFIELD)
        )
    }

    override fun getBuilds(characterId: String): List<CharacterBuild> =
        NteLocalDataSource.getBuilds(characterId)
}
