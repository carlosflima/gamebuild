package com.carlosflima.gamebuild.ui

import androidx.lifecycle.ViewModel
import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.data.GameRepositoryImpl
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameBuildUiState(
    val selectedGame: Game? = null,
    val characters: List<GameCharacter> = emptyList(),
    val selectedCharacter: GameCharacter? = null,
    val selectedBuild: CharacterBuild? = null,
    val errorMessage: String? = null
)

class GameBuildViewModel(private val repository: GameRepository = GameRepositoryImpl()) : ViewModel() {
    private val _uiState = MutableStateFlow(GameBuildUiState())
    val uiState: StateFlow<GameBuildUiState> = _uiState.asStateFlow()

    fun selectGame(game: Game) {
        runCatching { repository.getCharacters(game) }
            .onSuccess { characters ->
                _uiState.value = GameBuildUiState(selectedGame = game, characters = characters)
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Não foi possível carregar o jogo."
                )
            }
    }

    fun selectCharacter(character: GameCharacter) {
        runCatching { repository.getBuild(character.id) }
            .onSuccess { build ->
                _uiState.value = _uiState.value.copy(
                    selectedCharacter = character,
                    selectedBuild = build,
                    errorMessage = null
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = error.message ?: "Não foi possível carregar a build."
                )
            }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}
