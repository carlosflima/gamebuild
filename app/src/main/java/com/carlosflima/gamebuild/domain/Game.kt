package com.carlosflima.gamebuild.domain

enum class Game(
    val displayName: String,
    val shortName: String,
    val description: String,
    val isAvailable: Boolean
) {
    NTE("Neverness to Everness", "NTE", "Builds, characters and teams", true),
    WARFRAME("Warframe", "Warframe", "Em breve — Warframes, weapons and builds", false),
    ENDFIELD("Arknights: Endfield", "Endfield", "Em breve — Operators, weapons and teams", false)
}
