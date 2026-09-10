package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal interface LocalGameDataSource {
    val game: Game
    val characters: List<GameCharacter>

    fun getBuilds(characterId: String): List<CharacterBuild>
}
