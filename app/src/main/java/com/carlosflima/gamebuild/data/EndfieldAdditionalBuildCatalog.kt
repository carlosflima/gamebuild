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
                BuildSource(
                    name = "Endfield Hub — Estella Best Build Guide",
                    url = "https://endfieldhub.org/guides/progression/estella"
                ),
                BuildSource(
                    name = "Endfield Talos Wiki — Estella",
                    url = "https://endfield.wiki.gg/wiki/Estella"
                ),
                BuildSource(
                    name = "Endfield Talos Wiki — Aggeloslayer",
                    url = "https://endfield.wiki.gg/wiki/Aggeloslayer"
                ),
                BuildSource(
                    name = "Endfield Talos Wiki — AIC Light",
                    url = "https://endfield.wiki.gg/wiki/AIC_Light"
                )
            )
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> =
        builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> =
        buildsByCharacterId[characterId].orEmpty()
}
