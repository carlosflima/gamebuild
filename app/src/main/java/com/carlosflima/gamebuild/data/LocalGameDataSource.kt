package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.GameCharacter

internal interface LocalGameDataSource {
    val characters: List<GameCharacter>

    fun getBuilds(characterId: String): List<CharacterBuild>
}
