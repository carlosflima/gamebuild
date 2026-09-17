package com.carlosflima.gamebuild.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.carlosflima.gamebuild.data.LocalGameRepository
import com.carlosflima.gamebuild.data.GameRepository
import com.carlosflima.gamebuild.data.RemoteEndfieldRepository
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
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
    val selectedBuildType: BuildType? = null,
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

    val availableBuildTypes: List<BuildType>
        get() = builds.map { it.type }.distinct()

    val filteredBuilds: List<CharacterBuild>
        get() = selectedBuildType
            ?.let { type -> builds.filter { it.type == type } }
            ?: builds

    val comparisonBuilds: List<CharacterBuild>
        get() = if (selectedBuildType == null && availableBuildTypes.size > 1) builds else emptyList()
}

data class CatalogSyncState(
    val isRefreshing: Boolean = false,
    val publishedAt: String? = null,
    val failed: Boolean = false
)

class GameBuildViewModel(
    private val repository: GameRepository = LocalGameRepository(),
    private val savedStateHandle: SavedStateHandle = SavedStateHandle()
) : ViewModel() {
    private val _uiState = MutableStateFlow(restoreState())
    val uiState: StateFlow<GameBuildUiState> = _uiState.asStateFlow()
    private val _catalogSyncState = MutableStateFlow(CatalogSyncState())
    val catalogSyncState: StateFlow<CatalogSyncState> = _catalogSyncState.asStateFlow()
    private var catalogInitialized = false

    private var currentState: GameBuildUiState
        get() = _uiState.value
        set(value) {
            _uiState.value = value
            savedStateHandle[SELECTED_GAME] = value.selectedGame?.name
            savedStateHandle[CHARACTER_QUERY] = value.characterQuery
            savedStateHandle[CHARACTER_FILTER] = value.selectedCharacterFilter
            savedStateHandle[SELECTED_CHARACTER] = value.selectedCharacter?.id
            savedStateHandle[BUILD_TYPE] = value.selectedBuildType?.name
        }

    init {
        currentState = _uiState.value
    }

    fun initializeCatalog() {
        if (catalogInitialized) return
        catalogInitialized = true
        refreshEndfieldBuilds()
    }

    fun refreshEndfieldBuilds() {
        if (_catalogSyncState.value.isRefreshing) return
        _catalogSyncState.value = _catalogSyncState.value.copy(isRefreshing = true, failed = false)
        viewModelScope.launch {
            try {
                repository.loadCachedEndfieldBuilds()
                applyCatalogUpdate()
                val refreshed = repository.refreshEndfieldBuilds()
                applyCatalogUpdate()
                _catalogSyncState.value = _catalogSyncState.value.copy(failed = !refreshed)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _catalogSyncState.value = _catalogSyncState.value.copy(failed = true)
            } finally {
                _catalogSyncState.value = _catalogSyncState.value.copy(isRefreshing = false)
            }
        }
    }

    private fun applyCatalogUpdate() {
        _catalogSyncState.value = _catalogSyncState.value.copy(publishedAt = repository.endfieldCatalogDate)
        val state = currentState
        if (state.selectedGame != Game.ENDFIELD) return
        val character = state.selectedCharacter ?: return
        val builds = repository.getBuilds(Game.ENDFIELD, character.id)
        currentState = state.copy(
            builds = builds,
            selectedBuildType = state.selectedBuildType?.takeIf { type -> builds.any { it.type == type } }
        )
    }

    fun selectGame(game: Game) {
        if (!game.isAvailable) {
            currentState = currentState.copy(
                errorMessage = "Conteúdo de ${game.displayName} ainda está em preparação."
            )
            return
        }

        runCatching { repository.getCharacters(game) }
            .onSuccess { characters -> currentState = GameBuildUiState(selectedGame = game, characters = characters) }
            .onFailure { error -> currentState = currentState.copy(errorMessage = error.message ?: "Não foi possível carregar o jogo.") }
    }

    fun updateCharacterQuery(query: String) {
        currentState = currentState.copy(characterQuery = query)
    }

    fun toggleCharacterFilter(filter: String) {
        currentState = currentState.copy(
            selectedCharacterFilter = if (currentState.selectedCharacterFilter == filter) null else filter
        )
    }

    fun clearCharacterFilters() {
        currentState = currentState.copy(characterQuery = "", selectedCharacterFilter = null)
    }

    fun selectCharacter(character: GameCharacter) {
        val selectedGame = currentState.selectedGame
        if (selectedGame == null || character.game != selectedGame) return

        runCatching { repository.getBuilds(character.game, character.id) }
            .onSuccess { builds ->
                currentState = currentState.copy(
                    selectedCharacter = character,
                    builds = builds,
                    selectedBuildType = null,
                    errorMessage = null
                )
            }
            .onFailure { error ->
                currentState = currentState.copy(errorMessage = error.message ?: "Não foi possível carregar as builds.")
            }
    }

    fun selectBuildType(type: BuildType?) {
        if (type != null && type !in currentState.availableBuildTypes) return
        currentState = currentState.copy(selectedBuildType = type)
    }

    fun backToCharacters() {
        currentState = currentState.copy(
            selectedCharacter = null,
            builds = emptyList(),
            selectedBuildType = null,
            errorMessage = null
        )
    }

    fun backToGames() {
        currentState = GameBuildUiState()
    }

    fun clearError() { currentState = currentState.copy(errorMessage = null) }

    private fun savedString(key: String): String? = savedStateHandle.get<Any?>(key) as? String

    private fun restoreState(): GameBuildUiState {
        val game = Game.entries.firstOrNull {
            it.name == savedString(SELECTED_GAME) && it.isAvailable
        } ?: return GameBuildUiState()
        val characters = runCatching { repository.getCharacters(game) }.getOrElse { error ->
            return GameBuildUiState(errorMessage = error.message ?: "Não foi possível carregar o jogo.")
        }
        val rosterState = GameBuildUiState(
            selectedGame = game,
            characters = characters,
            characterQuery = savedString(CHARACTER_QUERY).orEmpty()
        )
        val filteredRosterState = rosterState.copy(
            selectedCharacterFilter = savedString(CHARACTER_FILTER)
                ?.takeIf { it in rosterState.characterFilters }
        )
        val character = characters.firstOrNull {
            it.id == savedString(SELECTED_CHARACTER) && it.game == game
        } ?: return filteredRosterState
        val builds = runCatching { repository.getBuilds(game, character.id) }.getOrElse { error ->
            return filteredRosterState.copy(
                errorMessage = error.message ?: "Não foi possível carregar as builds."
            )
        }
        return filteredRosterState.copy(
            selectedCharacter = character,
            builds = builds,
            selectedBuildType = BuildType.entries.firstOrNull { type ->
                type.name == savedString(BUILD_TYPE) && builds.any { it.type == type }
            }
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                GameBuildViewModel(
                    repository = RemoteEndfieldRepository.create(checkNotNull(this[APPLICATION_KEY])),
                    savedStateHandle = createSavedStateHandle()
                )
            }
        }

        private const val SELECTED_GAME = "selectedGame"
        private const val CHARACTER_QUERY = "characterQuery"
        private const val CHARACTER_FILTER = "characterFilter"
        private const val SELECTED_CHARACTER = "selectedCharacter"
        private const val BUILD_TYPE = "buildType"
    }
}
