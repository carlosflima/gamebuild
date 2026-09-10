package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal object WarframeCharacterCatalog {
    val characters: List<GameCharacter> = listOf(
        GameCharacter(
            id = "warframe-excalibur",
            name = "Excalibur",
            role = "Damage",
            game = Game.WARFRAME
        ),
        GameCharacter(
            id = "warframe-mag",
            name = "Mag",
            role = "Crowd Control",
            game = Game.WARFRAME
        ),
        GameCharacter(
            id = "warframe-volt",
            name = "Volt",
            role = "Damage / Support",
            game = Game.WARFRAME
        )
    )
}
