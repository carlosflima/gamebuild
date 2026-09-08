package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.GameCharacter

object NteLocalDataSource {
    val characters: List<GameCharacter> = NteCharacterCatalog.characters

    fun getBuilds(characterId: String): List<CharacterBuild> = NteBuildCatalog.getBuilds(characterId)
}
