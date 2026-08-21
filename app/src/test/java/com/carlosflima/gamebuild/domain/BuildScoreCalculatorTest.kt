package com.carlosflima.gamebuild.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildScoreCalculatorTest {

    @Test
    fun emptyBuild_returnsZeroScore() {
        val score = BuildScoreCalculator.calculate(
            CharacterBuild(characterId = "test", title = "Empty")
        )

        assertEquals(0, score.total)
        assertEquals(0, score.sourceCoverage)
        assertEquals(0, score.f2pAvailability)
        assertEquals(0, score.sourceAgreement)
    }

    @Test
    fun completeBuild_withThreeSources_scoresWithinExpectedRange() {
        val build = CharacterBuild(
            characterId = "zankou",
            title = "Zankou — V1.3",
            version = "1.3",
            arcRecommendation = "Ravenous Blade",
            cartridgeRecommendation = "Crimson: Twin Butterflies",
            modulePriority = listOf("CRIT", "Incantation DMG"),
            statPriority = listOf("CRIT Rate", "CRIT DMG", "ATK"),
            skillPriority = listOf("Ultimate", "Skill", "Basic"),
            teamRecommendation = listOf("Zankou", "Sakiri", "Support"),
            f2pTeam = listOf("Zankou", "Sakiri", "F2P Support"),
            f2pNote = "Alternative for free-to-play players",
            sources = listOf("Source A", "Source B", "Source C"),
            sourceUpdatedAt = "2026-08-21"
        )

        val score = BuildScoreCalculator.calculate(build)

        assertEquals(100, score.sourceCoverage)
        assertEquals(100, score.freshness)
        assertEquals(100, score.buildCompleteness)
        assertEquals(100, score.f2pAvailability)
        assertEquals(90, score.sourceAgreement)
        assertEquals(98, score.total)
    }

    @Test
    fun duplicateSources_doNotArtificiallyIncreaseCoverage() {
        val build = CharacterBuild(
            characterId = "test",
            title = "Duplicate sources",
            sources = listOf("Source A", "Source A", "Source A")
        )

        val score = BuildScoreCalculator.calculate(build)

        assertEquals(25, score.sourceCoverage)
        assertEquals(50, score.sourceAgreement)
    }

    @Test
    fun score_isAlwaysBoundedBetweenZeroAndOneHundred() {
        val build = CharacterBuild(
            characterId = "test",
            title = "Bounds",
            sources = (1..20).map { "Source $it" },
            f2pTeam = listOf("F2P")
        )

        val score = BuildScoreCalculator.calculate(build)

        assertTrue(score.total in 0..100)
        assertTrue(score.sourceCoverage in 0..100)
        assertTrue(score.freshness in 0..100)
        assertTrue(score.buildCompleteness in 0..100)
        assertTrue(score.f2pAvailability in 0..100)
        assertTrue(score.sourceAgreement in 0..100)
    }
}
