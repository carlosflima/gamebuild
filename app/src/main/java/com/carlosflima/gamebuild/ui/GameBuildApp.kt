package com.carlosflima.gamebuild.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GameBuildApp(viewModel: GameBuildViewModel = viewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("GameBuild — V0.2.1") }) }) { padding ->
            when {
                state.selectedCharacter != null -> CharacterBuildScreen(state.selectedCharacter!!, state.selectedBuild, viewModel, padding)
                state.selectedGame != null -> CharacterSelection(state.selectedGame!!, state.characters, state.filters, viewModel, padding)
                else -> GameSelection(viewModel, padding)
            }
            state.errorMessage?.let { message ->
                AlertDialog(onDismissRequest = viewModel::clearError, confirmButton = { Button(onClick = viewModel::clearError) { Text("OK") } }, title = { Text("Erro") }, text = { Text(message) })
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
private fun CharacterSelection(game: Game, characters: List<GameCharacter>, filters: NteFilters, viewModel: GameBuildViewModel, padding: PaddingValues) {
    val filtered = characters.filter { character ->
        (filters.query.isBlank() || character.name.contains(filters.query, ignoreCase = true)) &&
            (filters.element == null || character.element.equals(filters.element, ignoreCase = true)) &&
            (filters.role == null || character.role.equals(filters.role, ignoreCase = true))
    }
    val elements = characters.mapNotNull { it.element }.distinct().sorted()
    val roles = characters.map { it.role }.distinct().sorted()

    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Personagens — ${game.displayName}", style = MaterialTheme.typography.headlineSmall)
            OutlinedTextField(value = filters.query, onValueChange = viewModel::updateSearch, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("Buscar personagem") }, singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search))
        }
        if (elements.isNotEmpty()) item {
            Text("Elemento", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                elements.forEach { element -> FilterChip(selected = filters.element == element, onClick = { viewModel.updateElement(if (filters.element == element) null else element) }, label = { Text(element) }) }
            }
        }
        if (roles.isNotEmpty()) item {
            Text("Função", style = MaterialTheme.typography.labelLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(vertical = 6.dp)) {
                roles.forEach { role -> FilterChip(selected = filters.role == role, onClick = { viewModel.updateRole(if (filters.role == role) null else role) }, label = { Text(role) }) }
            }
            if (filters.query.isNotBlank() || filters.element != null || filters.role != null) AssistChip(onClick = viewModel::clearFilters, label = { Text("Limpar filtros") })
        }
        item { Text("${filtered.size} personagem(ns)", style = MaterialTheme.typography.labelMedium) }
        if (filtered.isEmpty()) item { Text("Nenhum personagem encontrado.", style = MaterialTheme.typography.bodyLarge) }
        items(filtered, key = { it.id }) { character ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CharacterImagePlaceholder(character)
                        Column(Modifier.weight(1f)) {
                            Text(character.name, style = MaterialTheme.typography.titleLarge)
                            Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "Elemento não informado"}")
                            Text("${character.role} • Arc: ${character.arcType ?: "não informado"} • Tier: ${character.tier ?: "?"}")
                        }
                    }
                    if (!character.imageUrl.isNullOrBlank()) Text("Imagem disponível na fonte", style = MaterialTheme.typography.labelSmall)
                    Button(onClick = { viewModel.selectCharacter(character) }) { Text("Ver build") }
                }
            }
        }
    }
}

@Composable
private fun CharacterImagePlaceholder(character: GameCharacter) {
    Card(Modifier.size(64.dp)) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Text(character.name.take(1).uppercase(), modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
private fun CharacterBuildScreen(character: GameCharacter, build: CharacterBuild?, viewModel: GameBuildViewModel, padding: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Button(onClick = viewModel::clearSelection) { Text("Voltar") }
            Text(character.name, style = MaterialTheme.typography.headlineMedium)
            Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "?"} • ${character.role}")
            Text("Arc: ${character.arcType ?: "Não informado"} • Tier: ${character.tier ?: "Não informado"}")
        }
        item {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(build?.title ?: "Build em pesquisa", style = MaterialTheme.typography.titleLarge)
                    if (build == null) Text("Ainda não há uma recomendação consolidada para este personagem.") else {
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
