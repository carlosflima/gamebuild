package com.carlosflima.gamebuild.domain

data class BuildScore(
    val total: Int,
    val sourceCoverage: Int,
    val freshness: Int,
    val buildCompleteness: Int,
    val f2pAvailability: Int,
    val sourceAgreement: Int
)

object BuildScoreCalculator {
    fun calculate(build: CharacterBuild): BuildScore {
        val sourceCoverage = (build.sources.distinct().size * 25).coerceAtMost(100)
        val freshness = if (build.version != null && !build.sourceUpdatedAt.isNullOrBlank()) 100 else 50
        val buildCompleteness = listOf(
            build.arcRecommendation,
            build.cartridgeRecommendation,
            build.modulePriority.takeIf { it.isNotEmpty() },
            build.statPriority.takeIf { it.isNotEmpty() },
            build.skillPriority.takeIf { it.isNotEmpty() },
            build.teamRecommendation.takeIf { it.isNotEmpty() }
        ).count { it != null }.let { it * 100 / 6 }
        val f2pAvailability = if (build.f2pTeam.isNotEmpty() || !build.f2pNote.isNullOrBlank()) 100 else 0
        val sourceAgreement = when {
            build.sources.distinct().size >= 3 -> 90
            build.sources.distinct().size == 2 -> 75
            build.sources.distinct().size == 1 -> 50
            else -> 0
        }

        val total = (
            sourceCoverage * 25 +
                freshness * 20 +
                buildCompleteness * 30 +
                f2pAvailability * 10 +
                sourceAgreement * 15
            ) / 100

        return BuildScore(
            total = total.coerceIn(0, 100),
            sourceCoverage = sourceCoverage,
            freshness = freshness,
            buildCompleteness = buildCompleteness,
            f2pAvailability = f2pAvailability,
            sourceAgreement = sourceAgreement
        )
    }
}
