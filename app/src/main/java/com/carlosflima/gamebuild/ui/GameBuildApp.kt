package com.carlosflima.gamebuild.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.carlosflima.gamebuild.R
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

private val GameBuildDarkColors = darkColorScheme(
    background = Color(0xFF080A0F),
    surface = Color(0xFF11151D),
    surfaceVariant = Color(0xFF1A202B),
    primary = Color(0xFF8B9DFF),
    secondary = Color(0xFF7DD3FC)
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GameBuildApp(viewModel: GameBuildViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val canNavigateBack = state.selectedGame != null

    BackHandler(enabled = canNavigateBack) {
        if (state.selectedCharacter != null) viewModel.backToCharacters() else viewModel.backToGames()
    }

    MaterialTheme(colorScheme = GameBuildDarkColors) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("GameBuild — V0.3") },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(
                                onClick = {
                                    if (state.selectedCharacter != null) viewModel.backToCharacters() else viewModel.backToGames()
                                }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_arrow_back),
                                    contentDescription = "Voltar"
                                )
                            }
                        }
                    }
                )
            }
        ) { padding ->
            when {
                state.selectedCharacter != null -> BuildScreen(state.selectedCharacter!!, state.builds, viewModel, padding)
                state.selectedGame != null -> CharacterSelection(
                    game = state.selectedGame!!,
                    characters = state.filteredCharacters,
                    query = state.characterQuery,
                    filters = state.characterFilters,
                    selectedFilter = state.selectedCharacterFilter,
                    onQueryChange = viewModel::updateCharacterQuery,
                    onFilterClick = viewModel::toggleCharacterFilter,
                    onClearFilters = viewModel::clearCharacterFilters,
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
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { Text("Selecione um jogo", style = MaterialTheme.typography.headlineSmall) }
        items(Game.entries, key = { it.name }) { game -> GameSelectionCard(game, viewModel) }
    }
}

@Composable
private fun GameSelectionCard(game: Game, viewModel: GameBuildViewModel) {
    Card(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(176.dp)) {
            Image(
                painter = painterResource(gameBackground(game)),
                contentDescription = "Fundo de ${game.displayName}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(game.displayName, style = MaterialTheme.typography.headlineSmall)
                Text(game.description, style = MaterialTheme.typography.bodyMedium)
                Button(onClick = { viewModel.selectGame(game) }, modifier = Modifier.padding(top = 10.dp)) {
                    Text("Selecionar")
                }
            }
        }
    }
}

private fun gameBackground(game: Game): Int = when (game) {
    Game.NTE -> R.drawable.bg_game_nte
    Game.WARFRAME -> R.drawable.bg_game_warframe
    Game.ENDFIELD -> R.drawable.bg_game_endfield
}

@Composable
private fun CharacterSelection(
    game: Game,
    characters: List<GameCharacter>,
    query: String,
    filters: List<String>,
    selectedFilter: String?,
    onQueryChange: (String) -> Unit,
    onFilterClick: (String) -> Unit,
    onClearFilters: () -> Unit,
    viewModel: GameBuildViewModel,
    padding: PaddingValues
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("Personagens — ${game.displayName}", style = MaterialTheme.typography.headlineSmall) }

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

        if (filters.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        filters.forEach { filter ->
                            FilterChip(
                                selected = selectedFilter == filter,
                                onClick = { onFilterClick(filter) },
                                label = { Text(filter) }
                            )
                        }
                    }
                    if (query.isNotBlank() || selectedFilter != null) {
                        TextButton(onClick = onClearFilters, contentPadding = PaddingValues(0.dp)) {
                            Text("Limpar filtros")
                        }
                    }
                }
            }
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
                Column {
                    Text(character.name, style = MaterialTheme.typography.headlineMedium)
                    Text(character.role, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (builds.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Sem builds disponíveis", style = MaterialTheme.typography.titleMedium)
                        Text("Os dados desta personagem ainda não foram adicionados à V0.3.")
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
            BuildVisualSection(
                title = "Arma",
                values = listOf(build.weapon),
                imageUrls = listOf(build.weaponImageUrl ?: buildItemImageUrl(build.weapon))
            )
            BuildVisualSection(
                title = "Equipamentos",
                values = build.equipment,
                imageUrls = build.equipment.mapIndexed { index, value ->
                    build.equipmentImageUrls.getOrNull(index) ?: buildItemImageUrl(value)
                }
            )
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
private fun BuildVisualSection(title: String, values: List<String>, imageUrls: List<String?>) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    values.forEachIndexed { index, value ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val imageUrl = imageUrls.getOrNull(index)
            if (imageUrl != null) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagem de $value",
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(value.take(1), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            Text(value, modifier = Modifier.width(230.dp))
        }
    }
}

private fun buildItemImageUrl(value: String): String? {
    val normalized = value.lowercase()
    return when {
        "ready-ready" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/3.webp"
        "raging flames" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/48.webp"
        "good boy's grand adventure" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/36.webp"
        "day off" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/13.webp"
        "camellia society" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/5.webp"
        "youthful fantasy" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/4.webp"
        "blow up the crowd" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/28.webp"
        "umbrella" in normalized -> "https://cdn.prydwen.gg/images/nte/weapons/10.webp"
        "speedy hedgehog" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/10.webp"
        "lost radiance" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/7.webp"
        "crimson: twin butterflies" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/1.webp"
        "diabolos" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/4.webp"
        "kingdom's guard" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/6.webp"
        "fireflies and the forest" in normalized -> "https://cdn.prydwen.gg/images/nte/sets/5.webp"
        "arc: solid" in normalized -> "https://cdn.prydwen.gg/images/nte/icons/arc_solid.webp"
        "arc: gas" in normalized -> "https://cdn.prydwen.gg/images/nte/icons/arc_gas.webp"
        "arc: liquid" in normalized -> "https://cdn.prydwen.gg/images/nte/icons/arc_liquid.webp"
        "arc: plasma" in normalized -> "https://cdn.prydwen.gg/images/nte/icons/arc_plasma.webp"
        "arc: synthesis" in normalized -> "https://cdn.prydwen.gg/images/nte/icons/arc_synthesis.webp"
        else -> null
    }
}

@Composable
private fun BuildSection(title: String, values: List<String>) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    values.forEach { value -> Text("• $value") }
}
