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
    private const val SOURCE = "https://nte.wiki/characters/"

    val characters = listOf(
        GameCharacter("nte-baicang", "Baicang", "DPS", Game.NTE, "S", "Incantation", "Synthesis", "S", sourceUrl = SOURCE),
        GameCharacter("nte-chaos", "Chaos", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-esper-zero", "Esper Zero", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-hotori", "Hotori", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-iroi", "Iroi", "Suporte", Game.NTE, "S", "Anima", "Synthesis", "S", sourceUrl = SOURCE),
        GameCharacter("nte-lacrimosa", "Lacrimosa", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-shinku", "Shinku", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-daffodill", "Daffodill", "DPS", Game.NTE, "S", "Chaos", "Liquid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-fadia", "Fadia", "Sobrevivência", Game.NTE, "S", "Psyche", "Liquid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-hathor", "Hathor", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-jiuyuan", "Jiuyuan", "DPS", Game.NTE, "S", "Lakshana", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-sakiri", "Sakiri", "Suporte", Game.NTE, "S", "Incantation", "Synthesis", "S", sourceUrl = SOURCE),
        GameCharacter("nte-skia", "Skia", "DPS", Game.NTE, "S", "Lakshana", "Synthesis", "S", sourceUrl = SOURCE),
        GameCharacter("nte-chiz", "Chiz", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-elyms", "Elyms", "DPS", Game.NTE, "S", "Cosmos", "Solid", "S", sourceUrl = SOURCE),
        GameCharacter("nte-nanally", "Nanally", "DPS", Game.NTE, "S", "Anima", "Synthesis", "S", sourceUrl = SOURCE),
        GameCharacter("nte-aurelia", "Aurelia", "DPS", Game.NTE, "A", "Psyche", "Liquid", "A", sourceUrl = SOURCE),
        GameCharacter("nte-haniel", "Haniel", "Suporte", Game.NTE, "A", "Psyche", "Liquid", "A", sourceUrl = SOURCE),
        GameCharacter("nte-mint", "Mint", "DPS", Game.NTE, "A", "Cosmos", "Solid", "A", sourceUrl = SOURCE),
        GameCharacter("nte-adler", "Adler", "Sobrevivência", Game.NTE, "A", "Incantation", "Synthesis", "A", sourceUrl = SOURCE),
        GameCharacter("nte-edgar", "Edgar", "Sobrevivência", Game.NTE, "A", "Cosmos", "Liquid", "A", sourceUrl = SOURCE)
    )

    val builds = mapOf(
        "nte-nanally" to CharacterBuild(
            characterId = "nte-nanally",
            title = "Nanally — DPS principal",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Nanally", "Sakiri", "Jiuyuan", "Zero"),
            f2pNote = "Priorizar alternativas acessíveis antes de investir em equipamentos premium.",
            sourceUrl = "https://www.neverness-to-everness.wiki/guide/Neverness-to-Everness-builds",
            sourceUpdatedAt = "2026-05-02"
        ),
        "nte-chiz" to CharacterBuild(
            characterId = "nte-chiz",
            title = "Chiz — DPS / Charge Engine",
            statPriority = listOf("CRIT", "ATK%", "Dano do elemento"),
            teamRecommendation = listOf("Chiz", "Hathor", "Jiuyuan", "Zero"),
            sourceUrl = "https://www.neverness-to-everness.wiki/guide/Neverness-to-Everness-builds",
            sourceUpdatedAt = "2026-05-02"
        )
    )
}
