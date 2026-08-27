package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

object NteLocalDataSource {
    val characters: List<GameCharacter> = listOf(
        GameCharacter(
            id = "nte-nanally",
            name = "Nanally",
            role = "Damage · Anima",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/nanally_card.webp"
        ),
        GameCharacter(
            id = "nte-sakiri",
            name = "Sakiri",
            role = "Buff · Incantation",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/sakiri_card.webp"
        ),
        GameCharacter(
            id = "nte-zero",
            name = "Zero",
            role = "Damage / Cycle enabler · Cosmos",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/zero_card.webp"
        ),
        GameCharacter(
            id = "nte-baicang",
            name = "Baicang",
            role = "Damage · Incantation",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/baicang_card.webp"
        ),
        GameCharacter(
            id = "nte-daffodill",
            name = "Daffodill",
            role = "Damage / Break · Chaos",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/daffodil_card.webp"
        ),
        GameCharacter(
            id = "nte-haniel",
            name = "Haniel",
            role = "Buff · Psyche",
            game = Game.NTE,
            imageUrl = "https://cdn.prydwen.gg/images/nte/characters/haniel_card.webp"
        ),
        GameCharacter(
            id = "nte-adler",
            name = "Adler",
            role = "Survival / Support · Incantation",
            game = Game.NTE
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> = mapOf(
        "nte-nanally" to listOf(
            CharacterBuild(
                id = "nte-nanally-meta",
                characterId = "nte-nanally",
                title = "On-field Damage",
                type = BuildType.META,
                version = "Patch 1 · revisado em 23/06/2026",
                weapon = "Ready-Ready (M1)",
                equipment = listOf("Arc: Plasma", "Cartridge: ajustar conforme composição e substats"),
                statPriority = listOf("Crit Rate", "Crit DMG", "ATK"),
                team = listOf("Nanally", "Sakiri", "Zero", "Flex Anima/Survival"),
                notes = "Nanally é uma DPS Anima de campo com foco em Follow-Up Attacks. A composição prioriza buffs, Esper Cycles e tempo de campo para manter sua janela de dano.",
                sources = listOf(BuildSource("Prydwen — Nanally Build", "https://www.prydwen.gg/neverness-to-everness/characters/nanally"))
            ),
            CharacterBuild(
                id = "nte-nanally-f2p",
                characterId = "nte-nanally",
                title = "Alternativa F2P",
                type = BuildType.F2P,
                version = "Patch 1 · revisado em 23/06/2026",
                weapon = "Raging Flames (M1)",
                equipment = listOf("Arc: Plasma", "Priorizar conjunto completo antes de substats perfeitos"),
                statPriority = listOf("Crit Rate", "Crit DMG", "ATK"),
                team = listOf("Nanally", "Zero", "Haniel", "Flex"),
                notes = "Raging Flames é indicada como a melhor opção F2P no guia consultado. Zero ajuda a acelerar os Esper Cycles e Haniel funciona como suporte acessível.",
                sources = listOf(BuildSource("Prydwen — Nanally Build", "https://www.prydwen.gg/neverness-to-everness/characters/nanally"))
            )
        ),
        "nte-sakiri" to listOf(
            CharacterBuild(
                id = "nte-sakiri-meta",
                characterId = "nte-sakiri",
                title = "Universal Buff / Scorch Support",
                type = BuildType.META,
                version = "Patch 1 · revisado em 26/05/2026",
                weapon = "Good Boy's Grand Adventure (M1)",
                equipment = listOf("Arc: Gas", "Cartridge: Speedy Hedgehog"),
                statPriority = listOf("Crit Rate / Crit DMG", "ATK", "Incantation DMG", "Cycle Intensity"),
                team = listOf("Baicang", "Sakiri", "Daffodill", "Incantation/Chaos Flex"),
                notes = "Sakiri exige pouco tempo de campo, agrupa inimigos e oferece ATK buff + redução de DEF. É especialmente forte em equipes Scorch e ainda funciona como suporte universal.",
                sources = listOf(BuildSource("Prydwen — Sakiri Build", "https://www.prydwen.gg/neverness-to-everness/characters/sakiri"))
            )
        ),
        "nte-zero" to listOf(
            CharacterBuild(
                id = "nte-zero-meta",
                characterId = "nte-zero",
                title = "Cycle Enabler",
                type = BuildType.META,
                version = "Patch 1 · revisado em 31/05/2026",
                weapon = "Day Off (M1)",
                equipment = listOf("Arc: Solid", "Cartridge: Speedy Hedgehog ou Lost Radiance"),
                statPriority = listOf("Energia / suporte ao ciclo", "Crit", "ATK"),
                team = listOf("Zero", "Nanally", "Anima Support", "Flex"),
                notes = "Apesar da função oficial Damage, o principal valor de Zero é completar instantaneamente um Esper Cycle com a Skill, simplificando rotações e habilitando reações com baixo tempo de campo.",
                sources = listOf(BuildSource("Prydwen — Zero Build", "https://www.prydwen.gg/neverness-to-everness/characters/zero"))
            )
        ),
        "nte-baicang" to listOf(
            CharacterBuild(
                id = "nte-baicang-meta",
                characterId = "nte-baicang",
                title = "Scorch Main DPS",
                type = BuildType.META,
                version = "Patch 1 · revisado em 26/05/2026",
                weapon = "Camellia Society (M1)",
                equipment = listOf("Arc: Synthesis", "Cartridge: Crimson: Twin Butterflies"),
                statPriority = listOf("Incantation DMG", "Crit Rate", "Crit DMG", "ATK", "Break Intensity"),
                team = listOf("Baicang", "Adler", "Sakiri", "Daffodill"),
                notes = "Baicang é uma DPS Incantation complexa, com forte sinergia com Scorch. O guia destaca Camellia Society e Crimson: Twin Butterflies, além da equipe Baicang/Adler/Sakiri/Daffodill.",
                sources = listOf(BuildSource("Prydwen — Baicang Build", "https://www.prydwen.gg/neverness-to-everness/characters/baicang"))
            )
        ),
        "nte-daffodill" to listOf(
            CharacterBuild(
                id = "nte-daffodill-meta",
                characterId = "nte-daffodill",
                title = "Quickswap Break DPS",
                type = BuildType.META,
                version = "Patch 1 · revisado em 26/05/2026",
                weapon = "Youthful Fantasy (M1)",
                equipment = listOf("Arc: Liquid", "Cartridge: Diabolos"),
                statPriority = listOf("Crit Rate", "Crit DMG", "Break Intensity", "Chaos DMG", "ATK"),
                team = listOf("Daffodill", "Incantation core", "Support", "Flex"),
                notes = "Daffodill funciona melhor em rotações quickswap, com forte pressão de Break e pouco tempo de campo. Diabolos fornece Chaos DMG e Chaos RES Ignore.",
                sources = listOf(BuildSource("Prydwen — Daffodill Build", "https://www.prydwen.gg/neverness-to-everness/characters/daffodil"))
            )
        ),
        "nte-haniel" to listOf(
            CharacterBuild(
                id = "nte-haniel-meta",
                characterId = "nte-haniel",
                title = "Universal ATK Buffer",
                type = BuildType.META,
                version = "Patch 1 · revisado em 26/05/2026",
                weapon = "Blow up the Crowd (M1)",
                equipment = listOf("Arc: Solid", "Cartridge: Speedy Hedgehog"),
                statPriority = listOf("Crit Rate", "Psyche DMG", "Crit DMG", "ATK", "Break Intensity"),
                team = listOf("Haniel", "Main DPS", "Chaos enabler", "Flex"),
                notes = "Haniel é um buffer universal de baixo tempo de campo. Sua assinatura aumenta o ataque do personagem ativo, enquanto Speedy Hedgehog melhora geração de Ultimate e adiciona buff de ATK ao time.",
                sources = listOf(BuildSource("Prydwen — Haniel Build", "https://www.prydwen.gg/neverness-to-everness/characters/haniel"))
            )
        ),
        "nte-adler" to listOf(
            CharacterBuild(
                id = "nte-adler-meta",
                characterId = "nte-adler",
                title = "Shield / Scorch Support",
                type = BuildType.META,
                version = "Patch 1 · revisado em 26/05/2026",
                weapon = "Umbrella (M5)",
                equipment = listOf("Arc: Synthesis", "Cartridge: Kingdom's Guard"),
                statPriority = listOf("DEF %", "DEF", "Break Intensity", "Cycle Intensity", "DMG"),
                team = listOf("Baicang", "Adler", "Sakiri", "Daffodill"),
                notes = "Adler combina escudo e debuffs com pouco tempo de campo. Umbrella fortalece sua sobrevivência e escudos; Kingdom's Guard prioriza DEF e potência de shield.",
                sources = listOf(BuildSource("Prydwen — Adler Build", "https://www.prydwen.gg/neverness-to-everness/characters/adler"))
            )
        )
    )

    fun getBuilds(characterId: String): List<CharacterBuild> = buildsByCharacterId[characterId].orEmpty()
}
