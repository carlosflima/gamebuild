package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal object EndfieldCharacterCatalog {
    val characters: List<GameCharacter> = listOf(
        GameCharacter(
            id = "endfield-endministrator",
            name = "Endministrator",
            role = "Guard · Physical",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-perlica",
            name = "Perlica",
            role = "Caster · Electric",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-chen-qianyu",
            name = "Chen Qianyu",
            role = "Guard · Physical",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-wulfgard",
            name = "Wulfgard",
            role = "Caster · Heat",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-yvonne",
            name = "Yvonne",
            role = "Striker · Cryo",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-typhoeus",
            name = "Typhoeus",
            role = "Striker · Nature",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-antal",
            name = "Antal",
            role = "Supporter · Electric",
            game = Game.ENDFIELD
        ),
        GameCharacter(
            id = "endfield-akekuri",
            name = "Akekuri",
            role = "Vanguard · Heat",
            game = Game.ENDFIELD
        )
    )
}
