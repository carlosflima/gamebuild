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
    val characterQuery: String = "",
    val selectedCharacterFilter: String? = null,
    val selectedCharacter: GameCharacter? = null,
    val builds: List<CharacterBuild> = emptyList(),
    val errorMessage: String? = null
) {
    val characterFilters: List<String>
        get() = characters
            .flatMap { character ->
                character.role.split("·", "/")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
            }
            .distinct()
            .sorted()

    val filteredCharacters: List<GameCharacter>
        get() {
            val query = characterQuery.trim()
            return characters.filter { character ->
                val matchesQuery = query.isEmpty() ||
                    character.name.contains(query, ignoreCase = true) ||
                    character.role.contains(query, ignoreCase = true)
                val matchesFilter = selectedCharacterFilter == null ||
                    character.role.contains(selectedCharacterFilter, ignoreCase = true)

                matchesQuery && matchesFilter
            }
        }
}

class GameBuildViewModel(private val repository: GameRepository = FakeGameRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(GameBuildUiState())
    val uiState: StateFlow<GameBuildUiState> = _uiState.asStateFlow()

    fun selectGame(game: Game) {
        if (!game.isAvailable) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Conteúdo de ${game.displayName} ainda está em preparação."
            )
            return
        }

        runCatching { repository.getCharacters(game) }
            .onSuccess { characters -> _uiState.value = GameBuildUiState(selectedGame = game, characters = characters) }
            .onFailure { error -> _uiState.value = _uiState.value.copy(errorMessage = error.message ?: "Não foi possível carregar o jogo.") }
    }

    fun updateCharacterQuery(query: String) {
        _uiState.value = _uiState.value.copy(characterQuery = query)
    }

    fun toggleCharacterFilter(filter: String) {
        _uiState.value = _uiState.value.copy(
            selectedCharacterFilter = if (_uiState.value.selectedCharacterFilter == filter) null else filter
        )
    }

    fun clearCharacterFilters() {
        _uiState.value = _uiState.value.copy(characterQuery = "", selectedCharacterFilter = null)
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
