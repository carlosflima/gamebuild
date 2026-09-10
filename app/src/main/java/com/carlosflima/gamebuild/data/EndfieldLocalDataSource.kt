package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal object EndfieldLocalDataSource : LocalGameDataSource {
    override val game: Game = Game.ENDFIELD
    override val characters: List<GameCharacter> = EndfieldCharacterCatalog.characters

    override fun getBuilds(characterId: String): List<CharacterBuild> =
        EndfieldBuildCatalog.getBuilds(characterId)
}
