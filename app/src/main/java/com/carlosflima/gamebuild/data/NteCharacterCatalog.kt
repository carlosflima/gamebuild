package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

internal object NteCharacterCatalog {
    val characters: List<GameCharacter> = listOf(
        GameCharacter(
            id = "nte-nanally",
            name = "Nanally",
            role = "Damage · Anima",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/nanally_card.webp"
        ),
        GameCharacter(
            id = "nte-sakiri",
            name = "Sakiri",
            role = "Buff · Incantation",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/sakiri_card.webp"
        ),
        GameCharacter(
            id = "nte-zero",
            name = "Zero",
            role = "Damage / Cycle enabler · Cosmos",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/zero_card.webp"
        ),
        GameCharacter(
            id = "nte-baicang",
            name = "Baicang",
            role = "Damage · Incantation",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/baicang_card.webp"
        ),
        GameCharacter(
            id = "nte-daffodill",
            name = "Daffodill",
            role = "Damage / Break · Chaos",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/daffodil_card.webp"
        ),
        GameCharacter(
            id = "nte-haniel",
            name = "Haniel",
            role = "Buff · Psyche",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/haniel_card.webp"
        ),
        GameCharacter(
            id = "nte-adler",
            name = "Adler",
            role = "Survival / Support · Incantation",
            game = Game.NTE
        )
    )
}
