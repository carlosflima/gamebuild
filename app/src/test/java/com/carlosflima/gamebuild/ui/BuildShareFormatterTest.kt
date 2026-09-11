package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildShareFormatterTest {
    @Test
    fun includesCharacterGameBuildDetailsAndSourceLinks() {
        val build = sampleBuild(
            id = "share-test",
            title = "Build de teste",
            type = BuildType.META,
            version = "1.0",
            weapon = "Arma teste"
        )

        val text = buildShareText(
            build = build,
            characterName = "Personagem teste",
            gameName = "Jogo teste",
            terms = AppTerms.Empty.forGame(Game.NTE)
        )

        assertTrue(text.contains("Personagem: Personagem teste"))
        assertTrue(text.contains("Jogo: Jogo teste"))
        assertTrue(text.contains("Build de teste"))
        assertTrue(text.contains("Meta • 1.0"))
        assertTrue(text.contains("Arma: Arma teste"))
        assertTrue(text.contains("Equipamentos: Item A • Item B"))
        assertTrue(text.contains("Prioridade de stats: ATK > Crit"))
        assertTrue(text.contains("Equipe: Aliado A • Aliado B"))
        assertTrue(text.contains("Notas importantes"))
        assertTrue(text.contains("Guia — https://example.com/guide"))
        assertTrue(text.contains("• Referência local"))
    }

    @Test
    fun comparisonIncludesContextAndEveryBuild() {
        val meta = sampleBuild(
            id = "meta",
            title = "Build Meta",
            type = BuildType.META,
            version = "1.0",
            weapon = "Arma Meta"
        )
        val f2p = sampleBuild(
            id = "f2p",
            title = "Build F2P",
            type = BuildType.F2P,
            version = "1.0",
            weapon = "Arma F2P"
        )

        val text = buildComparisonShareText(
            builds = listOf(meta, f2p),
            characterName = "Personagem teste",
            gameName = "Jogo teste",
            terms = AppTerms.Empty.forGame(Game.NTE)
        )

        assertTrue(text.contains("Comparação de builds"))
        assertTrue(text.contains("Personagem: Personagem teste"))
        assertTrue(text.contains("Jogo: Jogo teste"))
        assertTrue(text.contains("Build Meta"))
        assertTrue(text.contains("Meta • 1.0"))
        assertTrue(text.contains("Arma: Arma Meta"))
        assertTrue(text.contains("Build F2P"))
        assertTrue(text.contains("F2P • 1.0"))
        assertTrue(text.contains("Arma: Arma F2P"))
    }

    @Test
    fun warframeUsesModsVocabularyAndOmitsEmptySquad() {
        val build = sampleBuild(
            id = "warframe-share",
            title = "Starter",
            type = BuildType.F2P,
            version = "Update 43.5",
            weapon = "Exalted Blade",
            team = emptyList()
        )
        val terms = AppTerms.Empty.forGame(Game.WARFRAME)

        val text = buildShareText(
            build = build,
            characterName = "Excalibur",
            gameName = "Warframe",
            terms = terms
        )

        assertEquals("Warframes", terms.text("character.list.titlePrefix", "Personagens"))
        assertEquals("Buscar Warframe", terms.text("character.search.label", "Buscar personagem"))
        assertTrue(text.contains("Warframe: Excalibur"))
        assertTrue(text.contains("Mods: Item A • Item B"))
        assertFalse(text.contains("Equipamentos:"))
        assertFalse(text.contains("Esquadrão:"))
        assertFalse(text.contains("Equipe:"))
    }

    @Test
    fun endfieldUsesOperatorVocabularyAndKeepsEquipmentLabels() {
        val build = sampleBuild(
            id = "endfield-share",
            title = "Starter Physical DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Sundering Steel",
            team = emptyList()
        )
        val terms = AppTerms.Empty.forGame(Game.ENDFIELD)

        val text = buildShareText(
            build = build,
            characterName = "Endministrator",
            gameName = "Arknights: Endfield",
            terms = terms
        )

        assertEquals("Operadores", terms.text("character.list.titlePrefix", "Personagens"))
        assertEquals("Buscar operador", terms.text("character.search.label", "Buscar personagem"))
        assertEquals(
            "Nome, classe ou elemento",
            terms.text("character.search.placeholder", "Nome ou função")
        )
        assertTrue(text.contains("Operador: Endministrator"))
        assertTrue(text.contains("Arma: Sundering Steel"))
        assertTrue(text.contains("Equipamentos: Item A • Item B"))
        assertFalse(text.contains("Mods:"))
        assertFalse(text.contains("Equipe:"))
    }

    private fun sampleBuild(
        id: String,
        title: String,
        type: BuildType,
        version: String,
        weapon: String,
        team: List<String> = listOf("Aliado A", "Aliado B")
    ) = CharacterBuild(
        id = id,
        characterId = "character-1",
        title = title,
        type = type,
        version = version,
        weapon = weapon,
        equipment = listOf("Item A", "Item B"),
        statPriority = listOf("ATK", "Crit"),
        team = team,
        notes = "Notas importantes",
        sources = listOf(
            BuildSource("Guia", "https://example.com/guide"),
            BuildSource("Referência local")
        )
    )
}
