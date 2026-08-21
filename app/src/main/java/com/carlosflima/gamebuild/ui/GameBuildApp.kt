package com.carlosflima.gamebuild.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GameBuildApp(viewModel: GameBuildViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("GameBuild — V0.2") }) }) { padding ->
            when {
                state.selectedCharacter != null -> CharacterBuildScreen(state.selectedCharacter!!, state.selectedBuild, padding)
                state.selectedGame != null -> CharacterSelection(state.selectedGame!!, state.characters, viewModel, padding)
                else -> GameSelection(viewModel, padding)
            }
            state.errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    confirmButton = { Button(onClick = viewModel::clearError) { Text("OK") } },
                    title = { Text("Erro") }, text = { Text(message) }
                )
            }
        }
    }
}

@Composable
private fun GameSelection(viewModel: GameBuildViewModel, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Selecione um jogo", style = MaterialTheme.typography.headlineSmall)
        Game.entries.forEach { game ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(game.displayName, style = MaterialTheme.typography.titleLarge)
                    Text(game.description)
                    Button(onClick = { viewModel.selectGame(game) }) { Text("Selecionar") }
                }
            }
        }
    }
}

@Composable
private fun CharacterSelection(game: Game, characters: List<GameCharacter>, viewModel: GameBuildViewModel, padding: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Personagens — ${game.displayName}", style = MaterialTheme.typography.headlineSmall) }
        items(characters, key = { it.id }) { character ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(character.name, style = MaterialTheme.typography.titleLarge)
                    Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "Elemento não informado"}")
                    Text("${character.role} • Arc: ${character.arcType ?: "não informado"} • Tier: ${character.tier ?: "?"}")
                    Button(onClick = { viewModel.selectCharacter(character) }) { Text("Ver build") }
                }
            }
        }
    }
}

@Composable
private fun CharacterBuildScreen(character: GameCharacter, build: CharacterBuild?, padding: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(character.name, style = MaterialTheme.typography.headlineMedium)
            Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "?"} • ${character.role}")
            Text("Arc: ${character.arcType ?: "Não informado"} • Tier: ${character.tier ?: "Não informado"}")
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(build?.title ?: "Build em pesquisa", style = MaterialTheme.typography.titleLarge)
                    if (build == null) {
                        Text("Ainda não há uma recomendação consolidada para este personagem.")
                    } else {
                        build.arcRecommendation?.let { Text("Arc recomendada: $it") }
                        build.cartridgeRecommendation?.let { Text("Cartridges: $it") }
                        if (build.statPriority.isNotEmpty()) Text("Prioridade de stats: ${build.statPriority.joinToString(" → ")}")
                        if (build.teamRecommendation.isNotEmpty()) Text("Equipe: ${build.teamRecommendation.joinToString(" + ")}")
                        build.f2pNote?.let { Text("F2P: $it") }
                        build.sourceUpdatedAt?.let { Text("Fonte atualizada: $it") }
                        build.sourceUrl?.let { Text("Fonte: $it") }
                    }
                }
            }
        }
    }
}
