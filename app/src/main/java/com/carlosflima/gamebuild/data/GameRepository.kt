package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

interface GameRepository {
    fun getCharacters(game: Game): List<GameCharacter>
    fun getBuild(characterId: String): CharacterBuild?
}

class GameRepositoryImpl : GameRepository {
    override fun getCharacters(game: Game): List<GameCharacter> = when (game) {
        Game.NTE -> NteCatalog.characters
        Game.WARFRAME -> emptyList()
        Game.ENDFIELD -> emptyList()
    }

    override fun getBuild(characterId: String): CharacterBuild? = NteCatalog.builds[characterId]
}

private object NteCatalog {
    private const val WIKI_SOURCE = "https://nte.wiki/characters/"
    private const val GAMEWITH_SOURCE = "https://gamewith.net/nte/76171"
    private const val ALLTHINGS_SOURCE = "https://allthings.how/nte-neverness-to-everness-best-zankou-build-guide-and-teams/"
    private const val NEVERNESS_SOURCE = "https://neverness.gg/zankou-nte-build/"

    val characters = listOf(
        GameCharacter("nte-zankou", "Zankou", "DPS principal", Game.NTE, "S", "Incantation", "Gas", "S", sourceUrl = GAMEWITH_SOURCE),
        GameCharacter("nte-baicang", "Baicang", "DPS", Game.NTE, "S", "Incantation", "Synthesis", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-chaos", "Chaos", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-esper-zero", "Esper Zero", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-hotori", "Hotori", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-iroi", "Iroi", "Suporte", Game.NTE, "S", "Anima", "Synthesis", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-lacrimosa", "Lacrimosa", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-shinku", "Shinku", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-daffodill", "Daffodill", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-fadia", "Fadia", "Sobrevivência", Game.NTE, "S", "Psyche", "Liquid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-hathor", "Hathor", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-jiuyuan", "Jiuyuan", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-sakiri", "Sakiri", "Suporte", Game.NTE, "S", "Incantation", "Synthesis", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-skia", "Skia", "DPS", Game.NTE, "S", "Lakshana", "Synthesis", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-chiz", "Chiz", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-elyms", "Elyms", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-nanally", "Nanally", "DPS", Game.NTE, "S", "Anima", "Synthesis", "S", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-aurelia", "Aurelia", "DPS", Game.NTE, "A", "Psyche", "Liquid", "A", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-haniel", "Haniel", "Suporte", Game.NTE, "A", "Psyche", "Liquid", "A", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-mint", "Mint", "DPS", Game.NTE, "A", "Cosmos", "Solid", "A", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-adler", "Adler", "Sobrevivência", Game.NTE, "A", "Incantation", "Synthesis", "A", sourceUrl = WIKI_SOURCE),
        GameCharacter("nte-edgar", "Edgar", "Sobrevivência", Game.NTE, "A", "Cosmos", "Liquid", "A", sourceUrl = WIKI_SOURCE)
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
            f2pNote = "A composição F2P mantém Zankou como única personagem limitada e usa Daffodill, Mint e Adler para dano/reação e sobrevivência.",
            sources = listOf(GAMEWITH_SOURCE, ALLTHINGS_SOURCE, NEVERNESS_SOURCE),
            sourceUrl = GAMEWITH_SOURCE,
            sourceUpdatedAt = "2026-08-21"
        ),
        "nte-nanally" to CharacterBuild(
            characterId = "nte-nanally",
            title = "Nanally — DPS principal",
            version = "1.3",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Nanally", "Sakiri", "Jiuyuan", "Esper Zero"),
            f2pNote = "Priorizar alternativas acessíveis antes de investir em equipamentos premium.",
            sourceUrl = WIKI_SOURCE,
            sourceUpdatedAt = "2026-08-21"
        ),
        "nte-chiz" to CharacterBuild(
            characterId = "nte-chiz",
            title = "Chiz — DPS / Charge Engine",
            version = "1.3",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Chiz", "Hathor", "Jiuyuan", "Esper Zero"),
            sourceUrl = "https://gamewith.net/nte/74393",
            sourceUpdatedAt = "2026-08-21"
        )
    )
}
