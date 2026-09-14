package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild

internal object EndfieldAdditionalBuildCatalog {
    internal val builds: List<CharacterBuild> = listOf(
        CharacterBuild(
            id = "endfield-estella-f2p-starter-2026-09",
            characterId = "endfield-estella",
            title = "Starter Cryo Debuff Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Aggeloslayer",
            equipment = listOf(
                "AIC Light Armor",
                "AIC Tactical Gloves",
                "AIC Light Plate",
                "AIC Ceramic Plate"
            ),
            statPriority = listOf(
                "Will",
                "Physical DMG",
                "Ultimate Gain Efficiency"
            ),
            team = emptyList(),
            notes = "Accessible progression setup for Estella's Cryo Infliction, Lift and Physical Susceptibility utility. Aggeloslayer is a 4-star Polearm option with Will scaling, while AIC Light is an early Authority/Gear Assembly set. Prioritize Will and Physical damage for her debuff-oriented combo contribution, then Ultimate Gain Efficiency for smoother rotations without requiring premium weapons.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Estella Best Build Guide", url = "https://endfieldhub.org/guides/progression/estella"),
                BuildSource(name = "Endfield Talos Wiki — Estella", url = "https://endfield.wiki.gg/wiki/Estella"),
                BuildSource(name = "Endfield Talos Wiki — Aggeloslayer", url = "https://endfield.wiki.gg/wiki/Aggeloslayer"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light", url = "https://endfield.wiki.gg/wiki/AIC_Light")
            )
        ),
        CharacterBuild(
            id = "endfield-fluorite-f2p-starter-2026-09",
            characterId = "endfield-fluorite",
            title = "Starter Nature Infliction Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Howling Guard",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Arts Intensity", "Ultimate Gain Efficiency", "Nature DMG"),
            team = emptyList(),
            notes = "Accessible progression setup for Fluorite's Nature and Cryo Infliction support. Howling Guard is a 4-star Handcannon available through Arsenal Exchange, while AIC Light is a low-cost Authority/Gear Assembly set for early progression. Prioritize Arts Intensity and Ultimate Gain Efficiency for reliable elemental application, then Nature damage, without over-investing in premium weapons.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Fluorite Best Build Guide", url = "https://endfieldhub.org/guides/progression/fluorite"),
                BuildSource(name = "Endfield Talos Wiki — Fluorite", url = "https://endfield.wiki.gg/wiki/Fluorite"),
                BuildSource(name = "Endfield Talos Wiki — Howling Guard", url = "https://endfield.wiki.gg/wiki/Howling_Guard"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light", url = "https://endfield.wiki.gg/wiki/AIC_Light")
            )
        ),
        CharacterBuild(
            id = "endfield-ardelia-f2p-starter-2026-09",
            characterId = "endfield-ardelia",
            title = "Starter Nature Sustain Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Hypernova Auto",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("HP", "Treatment Efficiency", "Ultimate Gain Efficiency"),
            team = emptyList(),
            notes = "Accessible sustain-support setup for Ardelia's healing, Corrosion loop and dual Susceptibility utility. Hypernova Auto is a 4-star Arts Unit from Arsenal Exchange, while AIC Light is available through Authority progression and Gear Assembly. Prioritize HP and Treatment Efficiency for stronger sustain, then Ultimate Gain Efficiency for more reliable support rotations without requiring her 6-star login weapon.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Ardelia Best Build Guide", url = "https://endfieldhub.org/guides/progression/ardelia"),
                BuildSource(name = "Endfield Talos Wiki — Ardelia", url = "https://endfield.wiki.gg/wiki/Ardelia"),
                BuildSource(name = "Endfield Talos Wiki — Hypernova Auto", url = "https://endfield.wiki.gg/wiki/Hypernova_Auto"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-ember-f2p-starter-2026-09",
            characterId = "endfield-ember",
            title = "Starter Heat Tank",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Industry 0.1",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("HP", "Treatment Efficiency", "Defense"),
            team = emptyList(),
            notes = "Accessible tank-support setup for Ember's team shielding and reactive HP treatment. Industry 0.1 is a 4-star Great Sword suitable for early progression, while AIC Light is available through Authority progression and Gear Assembly. Prioritize HP for stronger Ultimate shields, then Treatment Efficiency and Defense for sustain, without requiring Finishing Call, Sundered Prince or other premium weapons.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Ember Best Build Guide", url = "https://endfieldhub.org/guides/progression/ember"),
                BuildSource(name = "Endfield Talos Wiki — Ember", url = "https://endfield.wiki.gg/wiki/Ember"),
                BuildSource(name = "Endfield Talos Wiki — Industry 0.1", url = "https://endfield.wiki.gg/wiki/Industry_0.1"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-lifeng-f2p-starter-2026-09",
            characterId = "endfield-lifeng",
            title = "Starter Physical Sub-DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Aggeloslayer",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Agility", "Physical DMG", "ATK"),
            team = emptyList(),
            notes = "Accessible Physical sub-DPS setup for Lifeng's grouping and Physical Susceptibility utility. Aggeloslayer is a 4-star Polearm that remains a low-cost mid-game option, while AIC Light is available through Authority progression and Gear Assembly. Prioritize Agility, Physical damage and ATK to support both his personal damage and Physical-team enabling without requiring premium Polearms.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Lifeng Best Build Guide", url = "https://endfieldhub.org/guides/progression/lifeng"),
                BuildSource(name = "Endfield Talos Wiki — Lifeng", url = "https://endfield.wiki.gg/wiki/Lifeng"),
                BuildSource(name = "Endfield Talos Wiki — Aggeloslayer", url = "https://endfield.wiki.gg/wiki/Aggeloslayer"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-pogranichnik-f2p-starter-2026-09",
            characterId = "endfield-pogranichnik",
            title = "Starter Physical SP Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Wave Tide",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("ATK", "Physical DMG", "Ultimate Gain Efficiency"),
            team = emptyList(),
            notes = "Accessible Physical Vanguard setup centered on Pogranichnik's Breach and SP-recovery utility. Wave Tide is a 4-star Sword that remains viable through mid-game progression, while AIC Light is available through Authority progression and Gear Assembly. Prioritize ATK and Physical damage for his support-DPS contribution, then Ultimate Gain Efficiency for smoother Shieldguard rotations without requiring Never Rest, Thermite Cutter or other premium weapons.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Pogranichnik Best Build Guide", url = "https://endfieldhub.org/guides/progression/pogranichnik"),
                BuildSource(name = "Endfield Talos Wiki — Pogranichnik", url = "https://endfield.wiki.gg/wiki/Pogranichnik"),
                BuildSource(name = "Endfield Talos Wiki — Wave Tide", url = "https://endfield.wiki.gg/wiki/Wave_Tide"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-last-rite-f2p-starter-2026-09",
            characterId = "endfield-last-rite",
            title = "Starter Cryo Burst DPS",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Industry 0.1",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("ATK", "Cryo DMG", "Arts Intensity"),
            team = emptyList(),
            notes = "Accessible Cryo burst setup for Last Rite's Cryo Infliction and Final Strike loop. Industry 0.1 is a 4-star Great Sword that provides a straightforward early-game ATK route, while AIC Light becomes available through Authority progression and Gear Assembly. Prioritize ATK and Cryo damage for burst windows, then Arts Intensity, without requiring Seeker of Dark Lung, Khravengger, Sundered Prince or other premium weapons.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Last Rite Best Build Guide", url = "https://endfieldhub.org/guides/progression/last-rite"),
                BuildSource(name = "Endfield Talos Wiki — Last Rite", url = "https://endfield.wiki.gg/wiki/Last_Rite"),
                BuildSource(name = "Endfield Talos Wiki — Industry 0.1", url = "https://endfield.wiki.gg/wiki/Industry_0.1"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-gilberta-f2p-starter-2026-09",
            characterId = "endfield-gilberta",
            title = "Starter Nature Grouping Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Hypernova Auto",
            equipment = listOf("AIC Light Armor", "AIC Tactical Gloves", "AIC Light Plate", "AIC Ceramic Plate"),
            statPriority = listOf("Intellect", "Ultimate Gain Efficiency", "Nature DMG"),
            team = emptyList(),
            notes = "Accessible Nature support setup for Gilberta's grouping, Lift and Arts Susceptibility utility. Hypernova Auto is a 4-star Arts Unit that remains useful through mid-game progression, while AIC Light is available through Authority progression and Gear Assembly. Prioritize Intellect for her support scaling, then Ultimate Gain Efficiency for more frequent gravity-field rotations and Nature damage for her personal contribution, without requiring premium Arts Units.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Gilberta Best Build Guide", url = "https://endfieldhub.org/guides/progression/gilberta"),
                BuildSource(name = "Endfield Talos Wiki — Gilberta", url = "https://endfield.wiki.gg/wiki/Gilberta"),
                BuildSource(name = "Endfield Talos Wiki — Hypernova Auto", url = "https://endfield.wiki.gg/wiki/Hypernova_Auto"),
                BuildSource(name = "Endfield Talos Wiki — AIC Light Armor", url = "https://endfield.wiki.gg/wiki/AIC_Light_Armor")
            )
        ),
        CharacterBuild(
            id = "endfield-liino-f2p-starter-2026-09",
            characterId = "endfield-liino",
            title = "Starter Electric Sustain Support",
            type = BuildType.F2P,
            version = "Dreamscape of Wind and Snow · 2026-09",
            weapon = "Aggeloslayer",
            equipment = listOf(
                "Basic PPE",
                "Basic Gloves",
                "Emergency Comm",
                "Emergency Compression Core"
            ),
            statPriority = listOf(
                "Will",
                "Treatment Efficiency",
                "Ultimate Gain Efficiency"
            ),
            team = emptyList(),
            notes = "Low-cost early progression setup for Liino's Vocalist and Cosmovoice sustain support. Aggeloslayer is an accessible 4-star Polearm with Will, while the Basic and Emergency pieces come from Authority progression and Gear Assembly and avoid over-investing before specialized support gear unlocks. Prioritize Will for her support scaling, then Treatment Efficiency and Ultimate Gain Efficiency for stronger healing and more reliable Dawnstar Concerto rotations without requiring premium Polearms.",
            sources = listOf(
                BuildSource(name = "Endfield Hub — Liino Best Build Guide", url = "https://endfieldhub.org/guides/progression/liino"),
                BuildSource(name = "Endfield Talos Wiki — Liino", url = "https://endfield.wiki.gg/wiki/Liino"),
                BuildSource(name = "Endfield Talos Wiki — Aggeloslayer", url = "https://endfield.wiki.gg/wiki/Aggeloslayer"),
                BuildSource(name = "Endfield Talos Wiki — Basic PPE", url = "https://endfield.wiki.gg/wiki/Basic_PPE")
            )
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> =
        builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> =
        buildsByCharacterId[characterId].orEmpty()
}
