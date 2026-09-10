package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal object WarframeLocalDataSource : LocalGameDataSource {
    override val game: Game = Game.WARFRAME
    override val characters: List<GameCharacter> = WarframeCharacterCatalog.characters

    override fun getBuilds(characterId: String): List<CharacterBuild> =
        WarframeBuildCatalog.getBuilds(characterId)
}
