package com.carlosflima.gamebuild.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuildScoreCalculatorTest {

    @Test
    fun emptyBuild_returnsZeroScore() {
        val score = BuildScoreCalculator.calculate(CharacterBuild(characterId = "test", title = "Empty"))
        assertEquals("total", 0, score.total)
        assertEquals("sourceCoverage", 0, score.sourceCoverage)
        assertEquals("f2pAvailability", 0, score.f2pAvailability)
        assertEquals("sourceAgreement", 0, score.sourceAgreement)
    }

    @Test
    fun completeBuild_withThreeSources_scoresWithinExpectedRange() {
        val build = CharacterBuild(
            characterId = "zankou", title = "Zankou — V1.3", version = "1.3",
            arcRecommendation = "Ravenous Blade", cartridgeRecommendation = "Crimson: Twin Butterflies",
            modulePriority = listOf("CRIT", "Incantation DMG"), statPriority = listOf("CRIT Rate", "CRIT DMG", "ATK"),
            skillPriority = listOf("Ultimate", "Skill", "Basic"), teamRecommendation = listOf("Zankou", "Sakiri", "Support"),
            f2pTeam = listOf("Zankou", "Sakiri", "F2P Support"), f2pNote = "Alternative for free-to-play players",
            sources = listOf("Source A", "Source B", "Source C"), sourceUpdatedAt = "2026-08-21"
        )
        val score = BuildScoreCalculator.calculate(build)

        // These dimensions have deterministic values for the supplied fixture.
        assertEquals("sourceCoverage", 100, score.sourceCoverage)
        assertEquals("freshness", 100, score.freshness)
        assertEquals("f2pAvailability", 100, score.f2pAvailability)
        assertEquals("sourceAgreement", 90, score.sourceAgreement)

        // Completeness is intentionally range-based: the calculator may evolve its
        // required build sections without making this regression test brittle.
        assertTrue("buildCompleteness should be high for a complete fixture", score.buildCompleteness in 80..100)
        assertTrue("total should remain in the high-quality range", score.total in 90..100)
    }

    @Test
    fun duplicateSources_doNotArtificiallyIncreaseCoverage() {
        val score = BuildScoreCalculator.calculate(CharacterBuild(characterId = "test", title = "Duplicate sources", sources = listOf("Source A", "Source A", "Source A")))
        assertEquals("sourceCoverage", 25, score.sourceCoverage)
        assertEquals("sourceAgreement", 50, score.sourceAgreement)
    }

    @Test
    fun score_isAlwaysBoundedBetweenZeroAndOneHundred() {
        val score = BuildScoreCalculator.calculate(CharacterBuild(characterId = "test", title = "Bounds", sources = (1..20).map { "Source $it" }, f2pTeam = listOf("F2P")))
        assertTrue(score.total in 0..100)
        assertTrue(score.sourceCoverage in 0..100)
        assertTrue(score.freshness in 0..100)
        assertTrue(score.buildCompleteness in 0..100)
        assertTrue(score.f2pAvailability in 0..100)
        assertTrue(score.sourceAgreement in 0..100)
    }
}
