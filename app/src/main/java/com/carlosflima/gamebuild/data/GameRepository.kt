package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import com.carlosflima.gamebuild.domain.NteCharacterStatus

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuild(characterId: String): CharacterBuild?
}

class GameRepositoryImpl : GameRepository {
    override fun getCharacters(game: Game): List<GameCharacter> = when (game) {
        Game.NTE -> NteCatalog.characters
        Game.WARFRAME -> WarframeCatalog.characters
        Game.ENDFIELD -> emptyList()
    }

    override fun getBuild(characterId: String): CharacterBuild? =
        NteCatalog.builds[characterId] ?: WarframeCatalog.builds[characterId]
}

private object WarframeCatalog {
    private const val PATCH_NOTES = "https://www.warframe.com/en/patch-notes"
    private const val REVENANT_SOURCE = "https://overframe.gg/items/arsenal/5952/revenant-prime/"
    private const val WISP_SOURCE = "https://overframe.gg/items/arsenal/6227/wisp/"
    private const val MESA_SOURCE = "https://overframe.gg/items/arsenal/2387/mesa-prime/"

    // V0.3 baseline: Warframe Update 43.5 (Amir's Shockwave), August 2026.
    // Community build recommendations are represented as a curated starting point and keep
    // their source URLs visible so they can be refreshed without changing the UI contract.
    val characters = listOf(
        GameCharacter(
            id = "warframe-revenant-prime",
            name = "Revenant Prime",
            role = "Tank / Weapons Platform",
            game = Game.WARFRAME,
            rarity = "Prime",
            element = "Sobrevivência",
            arcType = "Mesmer Skin",
            tier = "S",
            sourceUrl = REVENANT_SOURCE,
            versionIntroduced = "Update 32"
        ),
        GameCharacter(
            id = "warframe-wisp-prime",
            name = "Wisp Prime",
            role = "Suporte / Weapons Platform",
            game = Game.WARFRAME,
            rarity = "Prime",
            element = "Suporte",
            arcType = "Reservoirs",
            tier = "S",
            sourceUrl = WISP_SOURCE,
            versionIntroduced = "Update 33.6"
        ),
        GameCharacter(
            id = "warframe-mesa-prime",
            name = "Mesa Prime",
            role = "DPS",
            game = Game.WARFRAME,
            rarity = "Prime",
            element = "Dano",
            arcType = "Peacemaker",
            tier = "S",
            sourceUrl = MESA_SOURCE,
            versionIntroduced = "Update 24.2"
        )
    )

    val builds = mapOf(
        "warframe-revenant-prime" to CharacterBuild(
            characterId = "warframe-revenant-prime",
            title = "Revenant Prime — Mesmer Tank / Steel Path",
            version = "43.5",
            arcRecommendation = "Mesmer Skin + Weapons Platform",
            alternativeArcs = listOf("Reave one-shot", "Danse Macabre"),
            cartridgeRecommendation = "Molt Augmented / Arcane Energize",
            modulePriority = listOf(
                "Mesmer Shield",
                "Blind Rage",
                "Transient Fortitude",
                "Umbral Intensify",
                "Primed Continuity",
                "Primed Flow"
            ),
            statPriority = listOf("Ability Strength", "Duration", "Energy"),
            skillPriority = listOf("Mesmer Skin", "Reave", "Enthrall"),
            f2pNote = "Substitua mods Primed/Umbral por variantes normais enquanto progride; priorize Strength para aumentar as cargas de Mesmer Skin.",
            sources = listOf(REVENANT_SOURCE, PATCH_NOTES),
            sourceUrl = REVENANT_SOURCE,
            sourceUpdatedAt = "2026-08-27"
        ),
        "warframe-wisp-prime" to CharacterBuild(
            characterId = "warframe-wisp-prime",
            title = "Wisp Prime — Reservoir Support / Steel Path",
            version = "43.5",
            arcRecommendation = "Reservoirs + Breach Surge",
            alternativeArcs = listOf("Nourish weapons platform", "Roar support"),
            cartridgeRecommendation = "Molt Augmented / Arcane Energize",
            modulePriority = listOf(
                "Fused Reservoir",
                "Blind Rage",
                "Transient Fortitude",
                "Archon Stretch",
                "Primed Continuity",
                "Umbral Intensify"
            ),
            statPriority = listOf("Ability Strength", "Duration", "Range"),
            skillPriority = listOf("Reservoirs", "Breach Surge", "Wil-O-Wisp"),
            f2pNote = "Uma versão de baixo investimento pode usar Intensify, Continuity e Stretch normais mantendo foco em Strength e Duration.",
            sources = listOf(WISP_SOURCE, PATCH_NOTES),
            sourceUrl = WISP_SOURCE,
            sourceUpdatedAt = "2026-08-27"
        ),
        "warframe-mesa-prime" to CharacterBuild(
            characterId = "warframe-mesa-prime",
            title = "Mesa Prime — Peacemaker DPS / Steel Path",
            version = "43.5",
            arcRecommendation = "Peacemaker + Shatter Shield",
            alternativeArcs = listOf("Nourish", "Pillage"),
            cartridgeRecommendation = "Arcane Velocity / Molt Augmented",
            modulePriority = listOf(
                "Mesa's Waltz",
                "Primed Continuity",
                "Primed Flow",
                "Umbral Intensify",
                "Narrow Minded",
                "Streamline"
            ),
            statPriority = listOf("Duration", "Efficiency", "Ability Strength"),
            skillPriority = listOf("Peacemaker", "Shatter Shield", "Shooting Gallery"),
            f2pNote = "Use Continuity, Flow e Intensify normais até obter as versões Primed/Umbral; mantenha Efficiency suficiente para sustentar Peacemaker.",
            sources = listOf(MESA_SOURCE, PATCH_NOTES),
            sourceUrl = MESA_SOURCE,
            sourceUpdatedAt = "2026-08-27"
        )
    )
}

private object NteCatalog {
    private const val ROSTER_SOURCE = "https://neverness.gg/neverness-to-everness-characters/"
    private const val MOBALYTICS_SOURCE = "https://mobalytics.gg/never-to-everness/characters"
    private const val GAMEWITH_TIER_SOURCE = "https://gamewith.net/nte/74170"
    private const val GAMEWITH_TEAM_SOURCE = "https://gamewith.net/nte/74215"
    private const val ZANKOU_GAMEWITH = "https://gamewith.net/nte/76171"
    private const val ZANKOU_ALLTHINGS = "https://allthings.how/nte-neverness-to-everness-best-zankou-build-guide-and-teams/"
    private const val ZANKOU_NEVERNESS = "https://neverness.gg/zankou-nte-build/"

    // Baseline: NTE v1.3. Roster cross-checked against the August 18-21, 2026 databases.
    // imageUrl intentionally remains null until a stable, redistributable image URL is verified.
    val characters = listOf(
        GameCharacter("nte-baicang", "Baicang", "DPS", Game.NTE, "S", "Incantation", "Synthesis", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-chaos", "Chaos", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.1"),
        GameCharacter("nte-chiz", "Chiz", "DPS", Game.NTE, "S", "Cosmos", null, "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-daffodill", "Daffodill", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-zero", "Zero", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-fadia", "Fadia", "Sobrevivência", Game.NTE, "S", "Psyche", null, "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-hathor", "Hathor", "DPS", Game.NTE, "S", "Lakshana", "Plasma", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-hotori", "Hotori", "Suporte", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-iroi", "Iroi", "Sobrevivência", Game.NTE, "S", "Anima", null, "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.2"),
        GameCharacter("nte-jiuyuan", "Jiuyuan", "DPS", Game.NTE, "S", "Anima", "Solid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-lacrimosa", "Lacrimosa", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.1"),
        GameCharacter("nte-linko", "Linko", "DPS", Game.NTE, "S", "Anima", null, "S", sourceUrl = ROSTER_SOURCE, status = NteCharacterStatus.UPCOMING, versionIntroduced = "1.3"),
        GameCharacter("nte-nanally", "Nanally", "DPS", Game.NTE, "S", "Anima", "Plasma", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-sakiri", "Sakiri", "Suporte", Game.NTE, "S", "Incantation", "Gas", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-shinku", "Shinku", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.2"),
        GameCharacter("nte-zankou", "Zankou", "DPS", Game.NTE, "S", "Incantation", "Gas", "S", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.3"),
        GameCharacter("nte-adler", "Adler", "Sobrevivência", Game.NTE, "A", "Incantation", "Synthesis", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-aurelia", "Aurelia", "DPS", Game.NTE, "A", "Psyche", "Liquid", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-edgar", "Edgar", "Sobrevivência", Game.NTE, "A", "Cosmos", "Liquid", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-haniel", "Haniel", "Suporte", Game.NTE, "A", "Psyche", "Solid", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-mint", "Mint", "DPS", Game.NTE, "A", "Anima", "Liquid", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0"),
        GameCharacter("nte-skia", "Skia", "DPS", Game.NTE, "A", "Lakshana", "Gas", "A", sourceUrl = ROSTER_SOURCE, versionIntroduced = "1.0")
    )

    val builds = mapOf(
        "nte-zankou" to CharacterBuild(
            characterId = "nte-zankou",
            title = "Zankou — DPS Incantation / DoT",
            version = "1.3",
            arcRecommendation = "Ravenous Blade",
            alternativeArcs = listOf("Contemplative Cat", "Watch Your Heads!"),
            cartridgeRecommendation = "Crimson: Twin Butterflies",
            modulePriority = listOf("CRIT DMG", "CRIT Rate", "Incantation DMG", "ATK%"),
            statPriority = listOf("CRIT DMG", "CRIT Rate", "Incantation DMG", "ATK%"),
            skillPriority = listOf("Basic Attack", "Ultimate", "Passives"),
            teamRecommendation = listOf("Zankou", "Lacrimosa", "Sakiri", "Iroi"),
            f2pTeam = listOf("Zankou", "Daffodill", "Mint", "Adler"),
            f2pNote = "Composição de entrada; o sistema deverá marcar recomendações F2P como alternativas, não equivalentes à equipe meta.",
            sources = listOf(ZANKOU_GAMEWITH, ZANKOU_ALLTHINGS, ZANKOU_NEVERNESS),
            sourceUrl = ZANKOU_GAMEWITH,
            sourceUpdatedAt = "2026-08-21"
        ),
        "nte-nanally" to CharacterBuild(
            characterId = "nte-nanally",
            title = "Nanally — DPS principal",
            version = "1.3",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Nanally", "Sakiri", "Jiuyuan", "Zero"),
            sources = listOf(MOBALYTICS_SOURCE),
            sourceUrl = MOBALYTICS_SOURCE,
            sourceUpdatedAt = "2026-08-21"
        ),
        "nte-chiz" to CharacterBuild(
            characterId = "nte-chiz",
            title = "Chiz — DPS / Charge",
            version = "1.3",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Chiz", "Hathor", "Jiuyuan", "Zero"),
            sources = listOf(GAMEWITH_TEAM_SOURCE),
            sourceUrl = GAMEWITH_TEAM_SOURCE,
            sourceUpdatedAt = "2026-08-21"
        )
    )
}
