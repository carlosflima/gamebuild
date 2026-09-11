package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.Game

internal fun AppTerms.forGame(game: Game): AppTerms = when (game) {
    Game.WARFRAME -> mergedWith(
        AppTerms(
            mapOf(
                "character.list.titlePrefix" to
                    text("game.warframe.character.list.titlePrefix", "Warframes"),
                "character.search.label" to
                    text("game.warframe.character.search.label", "Buscar Warframe"),
                "character.search.placeholder" to
                    text("game.warframe.character.search.placeholder", "Nome ou função"),
                "character.empty.title" to
                    text("game.warframe.character.empty.title", "Nenhum Warframe encontrado"),
                "character.empty.body" to
                    text(
                        "game.warframe.character.empty.body",
                        "Tente buscar por outro nome ou função."
                    ),
                "build.share.character" to
                    text("game.warframe.build.share.character", "Warframe"),
                "build.section.equipment" to
                    text("game.warframe.build.section.mods", "Mods"),
                "build.section.team" to
                    text("game.warframe.build.section.squad", "Esquadrão")
            )
        )
    )
    Game.ENDFIELD -> mergedWith(
        AppTerms(
            mapOf(
                "character.list.titlePrefix" to
                    text("game.endfield.character.list.titlePrefix", "Operadores"),
                "character.search.label" to
                    text("game.endfield.character.search.label", "Buscar operador"),
                "character.search.placeholder" to
                    text("game.endfield.character.search.placeholder", "Nome, classe ou elemento"),
                "character.empty.title" to
                    text("game.endfield.character.empty.title", "Nenhum operador encontrado"),
                "character.empty.body" to
                    text(
                        "game.endfield.character.empty.body",
                        "Tente buscar por outro nome, classe ou elemento."
                    ),
                "build.share.character" to
                    text("game.endfield.build.share.character", "Operador")
            )
        )
    )
    Game.NTE -> this
}
