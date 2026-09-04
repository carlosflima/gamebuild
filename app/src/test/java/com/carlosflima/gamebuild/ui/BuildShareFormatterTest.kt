package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildShareFormatterTest {
    @Test
    fun includesBuildDetailsAndSourceLinks() {
        val build = CharacterBuild(
            id = "share-test",
            characterId = "character-1",
            title = "Build de teste",
            type = BuildType.META,
            version = "1.0",
            weapon = "Arma teste",
            equipment = listOf("Item A", "Item B"),
            statPriority = listOf("ATK", "Crit"),
            team = listOf("Aliado A", "Aliado B"),
            notes = "Notas importantes",
            sources = listOf(
                BuildSource("Guia", "https://example.com/guide"),
                BuildSource("Referência local")
            )
        )

        val text = buildShareText(build, AppTerms.Empty)

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
}
