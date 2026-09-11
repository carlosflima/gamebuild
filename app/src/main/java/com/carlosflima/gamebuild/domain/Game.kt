package com.carlosflima.gamebuild.domain

enum class Game(
    val displayName: String,
    val shortName: String,
    val description: String,
    val isAvailable: Boolean
) {
    NTE("Neverness to Everness", "NTE", "Builds, characters and teams", true),
    WARFRAME("Warframe", "Warframe", "Warframes, mods and starter builds", true),
    ENDFIELD("Arknights: Endfield", "Endfield", "Operators, weapons and teams", true)
}
