package com.carlosflima.gamebuild.data

import com.carlosflima.gamebuild.domain.BuildSource
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild

internal object WarframeBuildCatalog {
    internal val builds: List<CharacterBuild> = listOf(
        CharacterBuild(
            id = "warframe-excalibur-f2p-starter-43-5",
            characterId = "warframe-excalibur",
            title = "Beginner Exalted Blade",
            type = BuildType.F2P,
            version = "Update 43.5",
            weapon = "Exalted Blade",
            equipment = listOf(
                "Steel Charge",
                "Intensify",
                "Augur Secrets",
                "Vitality",
                "Continuity",
                "Streamline",
                "Flow",
                "Stretch",
                "Augur Reach"
            ),
            statPriority = listOf(
                "Ability Strength",
                "Ability Range",
                "Ability Efficiency",
                "Ability Duration",
                "Health"
            ),
            team = emptyList(),
            notes = "0 Forma beginner setup focused on Exalted Blade and Radial Blind. No Prime mods, Archon Shards or Helminth are required; keep energy efficiency and pool healthy while learning the base kit.",
            sources = listOf(
                BuildSource(
                    name = "Overframe — Shifty_Warframe_Builds",
                    url = "https://overframe.gg/build/1025214/excalibur/beginner-excalibur-no-forma-no-shards-no-helminth-needed/"
                ),
                BuildSource(
                    name = "Warframe — Update 43.5 patch notes",
                    url = "https://www.warframe.com/pt-br/patch-notes"
                )
            )
        ),
        CharacterBuild(
            id = "warframe-mag-f2p-starter-43-5",
            characterId = "warframe-mag",
            title = "Starter MR0-1",
            type = BuildType.F2P,
            version = "Update 43.5",
            weapon = "Any starter weapon",
            equipment = listOf(
                "Redirection",
                "Vitality",
                "Intensify",
                "Continuity",
                "Stretch",
                "Streamline"
            ),
            statPriority = listOf(
                "Ability Strength",
                "Ability Range",
                "Survivability",
                "Ability Efficiency",
                "Ability Duration"
            ),
            team = emptyList(),
            notes = "Starter progression setup: prioritize Vitality, Intensify and Stretch first, then add Redirection, Continuity and Streamline as capacity allows. The build focuses on Pull and early Magnetize utility rather than a specific weapon.",
            sources = listOf(
                BuildSource(
                    name = "Overframe — wainbowz.",
                    url = "https://overframe.gg/build/865426/mag/beginners-mr-0-1-mag-starter-build/"
                ),
                BuildSource(
                    name = "Warframe — Update 43.5 patch notes",
                    url = "https://www.warframe.com/pt-br/patch-notes"
                )
            )
        ),
        CharacterBuild(
            id = "warframe-volt-f2p-starter-43-5",
            characterId = "warframe-volt",
            title = "F2P Starter Run and Gun",
            type = BuildType.F2P,
            version = "Update 43.5",
            weapon = "Any starter weapon",
            equipment = listOf(
                "Energy Siphon",
                "Streamline",
                "Stretch",
                "Intensify",
                "Augur Message",
                "Redirection"
            ),
            statPriority = listOf(
                "Ability Efficiency",
                "Ability Range",
                "Ability Strength",
                "Ability Duration",
                "Shields"
            ),
            team = emptyList(),
            notes = "Low-cost MR2-oriented setup for learning Volt: Shock for early damage, Speed for mobility, Electric Shield for defense and Discharge for crowd control. Flow and Continuity are natural optional upgrades when available.",
            sources = listOf(
                BuildSource(
                    name = "Overframe — richard43",
                    url = "https://overframe.gg/build/323393/volt/f2p-starter-volt-full-loadout-walkthrough-and-upgrade-path/"
                ),
                BuildSource(
                    name = "Warframe — Update 43.5 patch notes",
                    url = "https://www.warframe.com/pt-br/patch-notes"
                )
            )
        )
    )

    private val buildsByCharacterId: Map<String, List<CharacterBuild>> =
        builds.groupBy { it.characterId }

    fun getBuilds(characterId: String): List<CharacterBuild> =
        buildsByCharacterId[characterId].orEmpty()
}
