package com.carlosflima.gamebuild.domain

data class GameCharacter(
    val id: String,
    val name: String,
    val role: String,
    val game: Game,
    val imageUrl: String? = null
)
