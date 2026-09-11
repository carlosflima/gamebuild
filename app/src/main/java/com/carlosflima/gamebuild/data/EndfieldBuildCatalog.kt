package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild

internal object EndfieldBuildCatalog {
    internal val builds: List<CharacterBuild> = listOf(
        CharacterBuild(
            id = "endfield-endministrator-f2p-starter-2026-09",
            characterId = "endfield-endministrator",
            title = "Starter Physical DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Sundering Steel",
            equipment = listOf("Roving MSGR Jacket", "Roving MSGR Fists", "Roving MSGR Flashlight", "Roving MSGR Gyro"),
            statPriority = listOf("Agility", "Physical DMG", "Strength"),
            team = emptyList(),
            notes = "Early-progression Physical DPS setup built around accessible Sundering Steel and the Roving MSGR set. Prioritize Agility and Physical damage, and keep HP high when practical to benefit from the set bonus.",
            sources = listOf(
                BuildSource("Game8 — Endministrator Best Build and Weapons", "https://game8.co/games/Arknights-Endfield/archives/523676"),
                BuildSource("Arknights: Endfield — Dreamscape of Wind and Snow update", "https://endfield.gryphline.com/en-us/news/5209")
            )
        ),
        CharacterBuild(
            id = "endfield-perlica-f2p-starter-2026-09",
            characterId = "endfield-perlica",
            title = "Starter Electric Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Wild Wanderer",
            equipment = listOf("Mordvolt Insulation Vest T1", "Mordvolt Insulation Gloves", "Mordvolt Insulation Wrench T1", "Mordvolt Insulation Wrench T1"),
            statPriority = listOf("Intellect", "Electric DMG", "Arts Intensity"),
            team = emptyList(),
            notes = "Accessible early-game support setup focused on Perlica's Electric Infliction and Electrification. Wild Wanderer adds team value while Mordvolt Insulation strengthens her own Arts damage during progression.",
            sources = listOf(
                BuildSource("Game8 — Perlica Best Build and Weapons", "https://game8.co/games/Arknights-Endfield/archives/523675"),
                BuildSource("Arknights: Endfield — Dreamscape of Wind and Snow update", "https://endfield.gryphline.com/en-us/news/5209")
            )
        ),
        CharacterBuild(
            id = "endfield-chen-qianyu-f2p-starter-2026-09",
            characterId = "endfield-chen-qianyu",
            title = "Starter Vulnerability Sub-DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Sundering Steel",
            equipment = listOf("Roving MSGR Jacket", "Roving MSGR Fists", "Roving MSGR Flashlight", "Roving MSGR Gyro"),
            statPriority = listOf("Agility", "Physical DMG", "Strength"),
            team = emptyList(),
            notes = "Early-game sub-DPS setup for applying Lift and Vulnerability while contributing Physical damage. Sundering Steel and Roving MSGR emphasize Agility and Physical damage with low progression overhead.",
            sources = listOf(
                BuildSource("Game8 — Chen Qianyu Build and Weapons", "https://game8.co/games/Arknights-Endfield/archives/523674"),
                BuildSource("Arknights: Endfield — Dreamscape of Wind and Snow update", "https://endfield.gryphline.com/en-us/news/5209")
            )
        ),
        CharacterBuild(
            id = "endfield-wulfgard-f2p-starter-2026-09",
            characterId = "endfield-wulfgard",
            title = "Starter Heat DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Rational Farewell",
            equipment = listOf("Aburrey Heavy Armor", "Armored MSGR Gloves T1", "Aburrey UV Lamp", "Aburrey UV Lamp"),
            statPriority = listOf("Strength", "Heat DMG", "Skill DMG"),
            team = emptyList(),
            notes = "Starter Heat setup for early progression. Rational Farewell supports Wulfgard's Strength and Heat damage, while the accessible gear mix emphasizes skill damage before endgame sets become practical.",
            sources = listOf(
                BuildSource("Game8 — Wulfgard Build and Weapons", "https://game8.co/games/Arknights-Endfield/archives/523673"),
                BuildSource("Arknights: Endfield — Dreamscape of Wind and Snow update", "https://endfield.gryphline.com/en-us/news/5209")
            )
        ),
        CharacterBuild(
            id = "endfield-yvonne-f2p-starter-2026-09",
            characterId = "endfield-yvonne",
            title = "Starter Cryo DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Howling Guard",
            equipment = listOf("Mordvolt Insulation Vest T1", "Mordvolt Insulation Gloves", "Mordvolt Insulation Wrench T1", "Mordvolt Insulation Battery"),
            statPriority = listOf("Intellect", "Cryo DMG", "Critical Rate"),
            team = emptyList(),
            notes = "Accessible early-game Cryo setup for Yvonne. Howling Guard is a lower-rarity alternative when premium handcannons are unavailable, while Mordvolt Insulation supports her Intellect-focused progression and Cryo damage before endgame gear becomes practical.",
            sources = listOf(
                BuildSource("Game8 — Yvonne Build and Weapons", "https://game8.co/games/Arknights-Endfield/archives/523664"),
                BuildSource("Endfield Talos Wiki — Howling Guard", "https://endfield.wiki.gg/wiki/Howling_Guard"),
                BuildSource("Arknights: Endfield — Dreamscape of Wind and Snow update", "https://endfield.gryphline.com/en-us/news/5209")
            )
        ),
        CharacterBuild(
            id = "endfield-typhoeus-f2p-2026-09",
            characterId = "endfield-typhoeus",
            title = "F2P Nature Burst",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Fluorescent Roc",
            equipment = listOf("Deep Rampart Armor", "Deep Rampart Gauntlets", "Deep Rampart Comms", "Deep Rampart Comms"),
            statPriority = listOf("Agility", "Arts Intensity", "Nature DMG"),
            team = emptyList(),
            notes = "F2P-oriented setup for the current Typhoeus snapshot. Fluorescent Roc is a 4-star Arts Unit alternative, while craftable Deep Rampart pieces align with her Agility scaling and repeated Nature Burst damage without requiring her signature or Battle Pass weapon.",
            sources = listOf(
                BuildSource("Prydwen — Typhoeus Best Build Guide · Patch 1.5", "https://www.prydwen.gg/arknights-endfield/characters/typhoeus"),
                BuildSource("Prydwen — Fluorescent Roc", "https://www.prydwen.gg/arknights-endfield/weapons"),
                BuildSource("Endfield Talos Wiki — Deep Rampart Armor formula", "https://endfield.wiki.gg/wiki/Deep_Rampart_Armor_(Formula)"),
                BuildSource("Arknights: Endfield — Winter Hunt", "https://endfield.gryphline.com/en-us/news/6172")
            )
        ),
        CharacterBuild(
            id = "endfield-antal-f2p-starter-2026-09",
            characterId = "endfield-antal",
            title = "Starter Electric Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Hypernova Auto",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Arts Intensity", "Ultimate Gain Efficiency", "Electric DMG"),
            team = emptyList(),
            notes = "Accessible progression setup for Antal's Electric support role. Hypernova Auto is a 4-star Arts Unit option, while AIC Light pieces are available through Authority progression and Gear Assembly. Prioritize Arts Intensity and Ultimate Gain so Focus, Susceptibility and Amp remain useful without requiring premium weapons.",
            sources = listOf(
                BuildSource("Endfield Hub — Antal Best Build Guide", "https://endfieldhub.org/guides/progression/antal"),
                BuildSource("Endfield Talos Wiki — Antal", "https://endfield.wiki.gg/wiki/Antal"),
                BuildSource("Endfield Talos Wiki — Hypernova Auto", "https://endfield.wiki.gg/wiki/Hypernova_Auto"),
                BuildSource("Endfield Talos Wiki — AIC Light Armor", "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-akekuri-f2p-starter-2026-09",
            characterId = "endfield-akekuri",
            title = "Starter Heat SP Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Wave Tide",
            equipment = listOf("Catastrophe Heavy Armor", "Catastrophe Gloves", "Catastrophe Gauze Cartridge", "Catastrophe Filter"),
            statPriority = listOf("Ultimate Gain Efficiency", "ATK", "Intellect"),
            team = emptyList(),
            notes = "F2P-oriented Heat support setup centered on Akekuri's SP recovery. Wave Tide is a 4-star Sword available through Arsenal Exchange, while Catastrophe supports faster Ultimate access and is obtainable through Authority progression and Gear Assembly. Prioritize Ultimate Gain Efficiency, ATK and Intellect for reliable team rotations.",
            sources = listOf(
                BuildSource("Endfield Hub — Akekuri Best Build Guide", "https://endfieldhub.org/guides/progression/akekuri"),
                BuildSource("Endfield Talos Wiki — Akekuri", "https://endfield.wiki.gg/wiki/Akekuri"),
                BuildSource("Endfield Talos Wiki — Wave Tide", "https://endfield.wiki.gg/wiki/Wave_Tide"),
                BuildSource("Endfield Talos Wiki — Catastrophe", "https://endfield.wiki.gg/wiki/Catastrophe")
            )
        ),
        CharacterBuild(
            id = "endfield-avywenna-f2p-starter-2026-09",
            characterId = "endfield-avywenna",
            title = "Starter Electric DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Aggeloslayer",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Arts Intensity", "Electric DMG", "ATK"),
            team = emptyList(),
            notes = "Accessible early-progression Electric DPS setup for Avywenna. Aggeloslayer is a 4-star Polearm from Arsenal Exchange whose Arts damage and battle-skill ATK effect fit her Thunderlance loop, while AIC Light provides inexpensive Authority-level gear before higher-tier sets are worth investing in.",
            sources = listOf(
                BuildSource("Endfield Hub — Avywenna Best Build Guide", "https://endfieldhub.org/guides/progression/avywenna"),
                BuildSource("Endfield Talos Wiki — Avywenna", "https://endfield.wiki.gg/wiki/Avywenna"),
                BuildSource("Endfield Talos Wiki — Aggeloslayer", "https://endfield.wiki.gg/wiki/Aggeloslayer"),
                BuildSource("Endfield Talos Wiki — AIC Light Armor", "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-catcher-f2p-starter-2026-09",
            characterId = "endfield-catcher",
            title = "Starter Physical Tank",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Quencher",
            equipment = listOf("Catastrophe Heavy Armor", "Catastrophe Gloves", "Catastrophe Gauze Cartridge", "Catastrophe Filter"),
            statPriority = listOf("HP", "Defense", "Ultimate Gain Efficiency"),
            team = emptyList(),
            notes = "F2P-oriented Physical tank setup for Catcher. Quencher is a 4-star Great Sword available through Arsenal Exchange, while Catastrophe accelerates Ultimate access for more frequent Weaken and protection windows. Prioritize HP and Defense first, then Ultimate Gain Efficiency for reliable defensive rotations.",
            sources = listOf(
                BuildSource("Endfield Hub — Catcher Best Build Guide", "https://endfieldhub.org/guides/progression/catcher"),
                BuildSource("Endfield Talos Wiki — Catcher", "https://endfield.wiki.gg/wiki/Catcher"),
                BuildSource("Endfield Talos Wiki — Quencher", "https://endfield.wiki.gg/wiki/Quencher"),
                BuildSource("Endfield Talos Wiki — Catastrophe gear", "https://endfield.wiki.gg/wiki/Gear")
            )
        ),
        CharacterBuild(
            id = "endfield-alesh-f2p-starter-2026-09",
            characterId = "endfield-alesh",
            title = "Starter Cryo SP Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Wave Tide",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Ultimate Gain Efficiency", "ATK", "Cryo DMG"),
            team = emptyList(),
            notes = "Accessible Cryo support setup centered on Alesh's SP recovery and Solidification utility. Wave Tide is a 4-star Sword available through Arsenal Exchange, and AIC Light is an Authority-level progression set. Prioritize Ultimate Gain Efficiency and ATK, then Cryo damage, without requiring Thermite Cutter or other premium weapons.",
            sources = listOf(
                BuildSource("Endfield Hub — Alesh Best Build Guide", "https://endfieldhub.org/guides/progression/alesh"),
                BuildSource("Endfield Talos Wiki — Alesh", "https://endfield.wiki.gg/wiki/Alesh"),
                BuildSource("Endfield Talos Wiki — Wave Tide", "https://endfield.wiki.gg/wiki/Wave_Tide"),
                BuildSource("Endfield Talos Wiki — AIC Light Armor", "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> = builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> = buildsByCharacterId[characterId].orEmpty()
}
