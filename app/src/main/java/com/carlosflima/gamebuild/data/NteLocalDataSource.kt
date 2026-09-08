package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.GameCharacter

object NteLocalDataSource : LocalGameDataSource {
    override val characters: List<GameCharacter> = NteCharacterCatalog.characters

    override fun getBuilds(characterId: String): List<CharacterBuild> =
        NteBuildCatalog.getBuilds(characterId)
}
