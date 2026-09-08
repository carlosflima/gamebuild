package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
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
            terms = AppTerms.Empty
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
            terms = AppTerms.Empty
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

    private fun sampleBuild(
        id: String,
        title: String,
        type: BuildType,
        version: String,
        weapon: String
    ) = CharacterBuild(
        id = id,
        characterId = "character-1",
        title = title,
        type = type,
        version = version,
        weapon = weapon,
        equipment = listOf("Item A", "Item B"),
        statPriority = listOf("ATK", "Crit"),
        team = listOf("Aliado A", "Aliado B"),
        notes = "Notas importantes",
        sources = listOf(
            BuildSource("Guia", "https://example.com/guide"),
            BuildSource("Referência local")
        )
    )
}
