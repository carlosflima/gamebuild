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
            equipment = listOf(
                "Roving MSGR Jacket",
                "Roving MSGR Fists",
                "Roving MSGR Flashlight",
                "Roving MSGR Gyro"
            ),
            statPriority = listOf(
                "Agility",
                "Physical DMG",
                "Strength"
            ),
            team = emptyList(),
            notes = "Early-progression Physical DPS setup built around accessible Sundering Steel and the Roving MSGR set. Prioritize Agility and Physical damage, and keep HP high when practical to benefit from the set bonus.",
            sources = listOf(
                BuildSource(
                    name = "Game8 — Endministrator Best Build and Weapons",
                    url = "https://game8.co/games/Arknights-Endfield/archives/523676"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Dreamscape of Wind and Snow update",
                    url = "https://endfield.gryphline.com/en-us/news/5209"
                )
            )
        ),
        CharacterBuild(
            id = "endfield-perlica-f2p-starter-2026-09",
            characterId = "endfield-perlica",
            title = "Starter Electric Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Wild Wanderer",
            equipment = listOf(
                "Mordvolt Insulation Vest T1",
                "Mordvolt Insulation Gloves",
                "Mordvolt Insulation Wrench T1",
                "Mordvolt Insulation Wrench T1"
            ),
            statPriority = listOf(
                "Intellect",
                "Electric DMG",
                "Arts Intensity"
            ),
            team = emptyList(),
            notes = "Accessible early-game support setup focused on Perlica's Electric Infliction and Electrification. Wild Wanderer adds team value while Mordvolt Insulation strengthens her own Arts damage during progression.",
            sources = listOf(
                BuildSource(
                    name = "Game8 — Perlica Best Build and Weapons",
                    url = "https://game8.co/games/Arknights-Endfield/archives/523675"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Dreamscape of Wind and Snow update",
                    url = "https://endfield.gryphline.com/en-us/news/5209"
                )
            )
        ),
        CharacterBuild(
            id = "endfield-chen-qianyu-f2p-starter-2026-09",
            characterId = "endfield-chen-qianyu",
            title = "Starter Vulnerability Sub-DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Sundering Steel",
            equipment = listOf(
                "Roving MSGR Jacket",
                "Roving MSGR Fists",
                "Roving MSGR Flashlight",
                "Roving MSGR Gyro"
            ),
            statPriority = listOf(
                "Agility",
                "Physical DMG",
                "Strength"
            ),
            team = emptyList(),
            notes = "Early-game sub-DPS setup for applying Lift and Vulnerability while contributing Physical damage. Sundering Steel and Roving MSGR emphasize Agility and Physical damage with low progression overhead.",
            sources = listOf(
                BuildSource(
                    name = "Game8 — Chen Qianyu Build and Weapons",
                    url = "https://game8.co/games/Arknights-Endfield/archives/523674"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Dreamscape of Wind and Snow update",
                    url = "https://endfield.gryphline.com/en-us/news/5209"
                )
            )
        ),
        CharacterBuild(
            id = "endfield-wulfgard-f2p-starter-2026-09",
            characterId = "endfield-wulfgard",
            title = "Starter Heat DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Rational Farewell",
            equipment = listOf(
                "Aburrey Heavy Armor",
                "Armored MSGR Gloves T1",
                "Aburrey UV Lamp",
                "Aburrey UV Lamp"
            ),
            statPriority = listOf(
                "Strength",
                "Heat DMG",
                "Skill DMG"
            ),
            team = emptyList(),
            notes = "Starter Heat setup for early progression. Rational Farewell supports Wulfgard's Strength and Heat damage, while the accessible gear mix emphasizes skill damage before endgame sets become practical.",
            sources = listOf(
                BuildSource(
                    name = "Game8 — Wulfgard Build and Weapons",
                    url = "https://game8.co/games/Arknights-Endfield/archives/523673"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Dreamscape of Wind and Snow update",
                    url = "https://endfield.gryphline.com/en-us/news/5209"
                )
            )
        ),
        CharacterBuild(
            id = "endfield-yvonne-f2p-starter-2026-09",
            characterId = "endfield-yvonne",
            title = "Starter Cryo DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Howling Guard",
            equipment = listOf(
                "Mordvolt Insulation Vest T1",
                "Mordvolt Insulation Gloves",
                "Mordvolt Insulation Wrench T1",
                "Mordvolt Insulation Battery"
            ),
            statPriority = listOf(
                "Intellect",
                "Cryo DMG",
                "Critical Rate"
            ),
            team = emptyList(),
            notes = "Accessible early-game Cryo setup for Yvonne. Howling Guard is a lower-rarity alternative when premium handcannons are unavailable, while Mordvolt Insulation supports her Intellect-focused progression and Cryo damage before endgame gear becomes practical.",
            sources = listOf(
                BuildSource(
                    name = "Game8 — Yvonne Build and Weapons",
                    url = "https://game8.co/games/Arknights-Endfield/archives/523664"
                ),
                BuildSource(
                    name = "Endfield Talos Wiki — Howling Guard",
                    url = "https://endfield.wiki.gg/wiki/Howling_Guard"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Dreamscape of Wind and Snow update",
                    url = "https://endfield.gryphline.com/en-us/news/5209"
                )
            )
        ),
        CharacterBuild(
            id = "endfield-typhoeus-f2p-2026-09",
            characterId = "endfield-typhoeus",
            title = "F2P Nature Burst",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Fluorescent Roc",
            equipment = listOf(
                "Deep Rampart Armor",
                "Deep Rampart Gauntlets",
                "Deep Rampart Comms",
                "Deep Rampart Comms"
            ),
            statPriority = listOf(
                "Agility",
                "Arts Intensity",
                "Nature DMG"
            ),
            team = emptyList(),
            notes = "F2P-oriented setup for the current Typhoeus snapshot. Fluorescent Roc is a 4-star Arts Unit alternative, while craftable Deep Rampart pieces align with her Agility scaling and repeated Nature Burst damage without requiring her signature or Battle Pass weapon.",
            sources = listOf(
                BuildSource(
                    name = "Prydwen — Typhoeus Best Build Guide · Patch 1.5",
                    url = "https://www.prydwen.gg/arknights-endfield/characters/typhoeus"
                ),
                BuildSource(
                    name = "Prydwen — Fluorescent Roc",
                    url = "https://www.prydwen.gg/arknights-endfield/weapons"
                ),
                BuildSource(
                    name = "Endfield Talos Wiki — Deep Rampart Armor formula",
                    url = "https://endfield.wiki.gg/wiki/Deep_Rampart_Armor_(Formula)"
                ),
                BuildSource(
                    name = "Arknights: Endfield — Winter Hunt",
                    url = "https://endfield.gryphline.com/en-us/news/6172"
                )
            )
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> =
        builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> =
        buildsByCharacterId[characterId].orEmpty()
}
