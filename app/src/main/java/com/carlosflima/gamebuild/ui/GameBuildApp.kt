package com.carlosflima.gamebuild.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
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
                state.selectedCharacter != null -> BuildScreen(state.selectedCharacter!!, state.builds, viewModel, padding)
                state.selectedGame != null -> CharacterSelection(
                    game = state.selectedGame!!,
                    characters = state.filteredCharacters,
                    query = state.characterQuery,
                    onQueryChange = viewModel::updateCharacterQuery,
                    viewModel = viewModel,
                    padding = padding
                )
                else -> GameSelection(viewModel, padding)
            }

            state.errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = viewModel::clearError,
                    confirmButton = { Button(onClick = viewModel::clearError) { Text("OK") } },
                    title = { Text("Erro") },
                    text = { Text(message) }
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
private fun CharacterSelection(
    game: Game,
    characters: List<GameCharacter>,
    query: String,
    onQueryChange: (String) -> Unit,
    viewModel: GameBuildViewModel,
    padding: PaddingValues
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Personagens — ${game.displayName}", style = MaterialTheme.typography.headlineSmall)
                TextButton(onClick = viewModel::backToGames) { Text("Jogos") }
            }
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Buscar personagem") },
                placeholder = { Text("Nome, função ou elemento") }
            )
        }

        if (characters.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Nenhum personagem encontrado", style = MaterialTheme.typography.titleMedium)
                        Text("Tente buscar por outro nome, função ou elemento.")
                    }
                }
            }
        } else {
            items(characters, key = { it.id }) { character ->
                Card(Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        CharacterImage(character, Modifier.fillMaxWidth().height(180.dp))
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(character.name, style = MaterialTheme.typography.titleLarge)
                            Text(character.role)
                            Button(onClick = { viewModel.selectCharacter(character) }) { Text("Ver builds") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildScreen(character: GameCharacter, builds: List<CharacterBuild>, viewModel: GameBuildViewModel, padding: PaddingValues) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CharacterImage(character, Modifier.fillMaxWidth().height(220.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(character.name, style = MaterialTheme.typography.headlineMedium)
                        Text(character.role, style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = viewModel::backToCharacters) { Text("Voltar") }
                }
            }
        }

        if (builds.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sem builds disponíveis", style = MaterialTheme.typography.titleMedium)
                        Text("Os dados desta personagem ainda não foram adicionados à V0.2.")
                    }
                }
            }
        } else {
            items(builds, key = { it.id }) { build -> BuildCard(build) }
        }
    }
}

@Composable
private fun CharacterImage(character: GameCharacter, modifier: Modifier = Modifier) {
    if (character.imageUrl != null) {
        AsyncImage(
            model = character.imageUrl,
            contentDescription = "Imagem de ${character.name}",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(character.name.take(1), style = MaterialTheme.typography.displayMedium)
        }
    }
}

@Composable
private fun BuildCard(build: CharacterBuild) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(build.title, style = MaterialTheme.typography.titleLarge)
            Text("${build.type.displayName} • ${build.version}", style = MaterialTheme.typography.labelLarge)
            HorizontalDivider()
            BuildSection("Arma", listOf(build.weapon))
            BuildSection("Equipamentos", build.equipment)
            BuildSection("Prioridade de stats", build.statPriority)
            BuildSection("Equipe", build.team)
            HorizontalDivider()
            Text("Notas", style = MaterialTheme.typography.titleMedium)
            Text(build.notes)

            if (build.sources.isNotEmpty()) {
                HorizontalDivider()
                Text("Fontes", style = MaterialTheme.typography.titleMedium)
                build.sources.forEach { source ->
                    if (source.url != null) {
                        TextButton(
                            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(source.url))) },
                            contentPadding = PaddingValues(0.dp)
                        ) { Text(source.name) }
                    } else {
                        Text("• ${source.name}")
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildSection(title: String, values: List<String>) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    values.forEach { value -> Text("• $value") }
}
