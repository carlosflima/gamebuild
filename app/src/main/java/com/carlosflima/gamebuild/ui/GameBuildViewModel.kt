package com.carlosflima.gamebuild.ui

import androidx.lifecycle.ViewModel
import com.carlosflima.gamebuild.data.FakeGameRepository
import com.carlosflima.gamebuild.data.GameRepository
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
    val builds: List<CharacterBuild> = emptyList(),
    val errorMessage: String? = null
)

class GameBuildViewModel(private val repository: GameRepository = FakeGameRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(GameBuildUiState())
    val uiState: StateFlow<GameBuildUiState> = _uiState.asStateFlow()

    fun selectGame(game: Game) {
        runCatching { repository.getCharacters(game) }
            .onSuccess { characters -> _uiState.value = GameBuildUiState(selectedGame = game, characters = characters) }
            .onFailure { error -> _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Não foi possível carregar o jogo.") }
    }

    fun selectCharacter(character: GameCharacter) {
        runCatching { repository.getBuilds(character.id) }
            .onSuccess { builds ->
                _uiState.value = _uiState.value.copy(
                    selectedCharacter = character,
                    builds = builds,
                    errorMessage = null
                )
            }
            .onFailure { error ->
                _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Não foi possível carregar as builds.")
            }
    }

    fun backToCharacters() {
        _uiState.value = _uiState.value.copy(selectedCharacter = null, builds = emptyList(), errorMessage = null)
    }

    fun backToGames() {
        _uiState.value = GameBuildUiState()
    }

    fun clearError() { _uiState.value = _uiState.value.copy(errorMessage = null) }
}
