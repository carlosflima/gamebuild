package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class GameTermsTest {

    @Test
    fun `nte keeps the base terms instance`() {
        val base = AppTerms(mapOf("shared.term" to "Preservado"))

        assertSame(base, base.forGame(Game.NTE))
    }

    @Test
    fun `warframe overrides game vocabulary and preserves shared terms`() {
        val base = AppTerms(
            mapOf(
                "shared.term" to "Preservado",
                "game.warframe.character.search.label" to "Pesquisar Warframe"
            )
        )

        val terms = base.forGame(Game.WARFRAME)

        assertEquals("Warframes", terms.text("character.list.titlePrefix", "fallback"))
        assertEquals("Pesquisar Warframe", terms.text("character.search.label", "fallback"))
        assertEquals("Mods", terms.text("build.section.equipment", "fallback"))
        assertEquals("Esquadrão", terms.text("build.section.team", "fallback"))
        assertEquals("Warframe", terms.text("build.share.character", "fallback"))
        assertEquals("Preservado", terms.text("shared.term", "fallback"))
    }

    @Test
    fun `endfield overrides operator vocabulary and preserves shared sections`() {
        val base = AppTerms(
            mapOf(
                "shared.term" to "Preservado",
                "build.section.equipment" to "Equipamentos",
                "build.section.team" to "Equipe"
            )
        )

        val terms = base.forGame(Game.ENDFIELD)

        assertEquals("Operadores", terms.text("character.list.titlePrefix", "fallback"))
        assertEquals("Buscar operador", terms.text("character.search.label", "fallback"))
        assertEquals("Nome, classe ou elemento", terms.text("character.search.placeholder", "fallback"))
        assertEquals("Operador", terms.text("build.share.character", "fallback"))
        assertEquals("Equipamentos", terms.text("build.section.equipment", "fallback"))
        assertEquals("Equipe", terms.text("build.section.team", "fallback"))
        assertEquals("Preservado", terms.text("shared.term", "fallback"))
    }
}
