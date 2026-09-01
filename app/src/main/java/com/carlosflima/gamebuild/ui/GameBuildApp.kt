package com.carlosflima.gamebuild.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild
import com.carlosflima.gamebuild.domain.Game
import com.carlosflima.gamebuild.domain.GameCharacter

private val GameBuildDarkColors = darkColorScheme(
    background = Color(0xFF080A0F),
    surface = Color(0xE611151D),
    surfaceVariant = Color(0xE61A202B),
    primary = Color(0xFF8B9DFF),
    secondary = Color(0xFF7DD3FC)
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun GameBuildApp(
    terms: AppTerms = AppTerms.Empty,
    viewModel: GameBuildViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val canNavigateBack = state.selectedGame != null

    BackHandler(enabled = canNavigateBack) {
        if (state.selectedCharacter != null) viewModel.backToCharacters() else viewModel.backToGames()
    }

    MaterialTheme(colorScheme = GameBuildDarkColors) {
        Box(Modifier.fillMaxSize()) {
            AppBackground(
                selectedGame = state.selectedGame,
                selectedCharacter = state.selectedCharacter
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = if (state.selectedCharacter != null) 0.74f else 0.66f)
                    )
            )

            Scaffold(
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        title = { Text(terms.text("app.title", "Game Builds — V0.3")) },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                        navigationIcon = {
                            if (canNavigateBack) {
                                IconButton(
                                    onClick = {
                                        if (state.selectedCharacter != null) viewModel.backToCharacters()
                                        else viewModel.backToGames()
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_arrow_back),
                                        contentDescription = terms.text("common.back", "Voltar")
                                    )
                                }
                            }
                        }
                    )
                }
            ) { padding ->
                when {
                    state.selectedCharacter != null -> BuildScreen(
                        character = state.selectedCharacter!!,
                        builds = state.filteredBuilds,
                        comparisonBuilds = state.comparisonBuilds,
                        buildTypes = state.availableBuildTypes,
                        selectedBuildType = state.selectedBuildType,
                        onBuildTypeChange = viewModel::selectBuildType,
                        terms = terms,
                        padding = padding
                    )
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
                        terms = terms,
                        padding = padding
                    )
                    else -> GameSelection(viewModel, terms, padding)
                }

                state.errorMessage?.let { message ->
                    AlertDialog(
                        onDismissRequest = viewModel::clearError,
                        confirmButton = {
                            Button(onClick = viewModel::clearError) {
                                Text(terms.text("common.ok", "OK"))
                            }
                        },
                        title = { Text(terms.text("common.error", "Erro")) },
                        text = { Text(message) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppBackground(
    selectedGame: Game?,
    selectedCharacter: GameCharacter?
) {
    when {
        selectedCharacter != null -> {
            Box(Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(characterFallbackBackground(selectedCharacter.game)),
                    contentDescription = "Fundo de ${selectedCharacter.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                selectedCharacter.imageUrl?.let { imageUrl ->
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Fundo de ${selectedCharacter.name}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        selectedGame != null -> {
            Image(
                painter = painterResource(gamePageBackground(selectedGame)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        else -> {
            Image(
                painter = painterResource(R.drawable.app_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
private fun GameSelection(
    viewModel: GameBuildViewModel,
    terms: AppTerms,
    padding: PaddingValues
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                terms.text("game.select.title", "Selecione um jogo"),
                style = MaterialTheme.typography.headlineSmall
            )
        }
        items(Game.entries, key = { it.name }) { game ->
            GameSelectionCard(game, viewModel, terms)
        }
    }
}

@Composable
private fun GameSelectionCard(
    game: Game,
    viewModel: GameBuildViewModel,
    terms: AppTerms
) {
    val gameName = terms.gameName(game)
    val gameDescription = terms.gameDescription(game)

    Card(Modifier.fillMaxWidth()) {
        Box(Modifier.fillMaxWidth().height(176.dp)) {
            Image(
                painter = painterResource(gameBackground(game)),
                contentDescription = "Fundo de $gameName",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.42f)))
            Column(
                modifier = Modifier.fillMaxSize().padding(18.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(gameName, style = MaterialTheme.typography.headlineSmall)
                Text(gameDescription, style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = { viewModel.selectGame(game) },
                    enabled = game.isAvailable,
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    Text(
                        if (game.isAvailable) {
                            terms.text("game.select.button", "Selecionar")
                        } else {
                            terms.text("game.select.comingSoon", "Em breve")
                        }
                    )
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

private fun gamePageBackground(game: Game): Int = when (game) {
    Game.NTE -> R.drawable.bg_page_nte
    Game.WARFRAME -> R.drawable.bg_page_warframe
    Game.ENDFIELD -> R.drawable.bg_page_endfield
}

private fun characterFallbackBackground(game: Game): Int = when (game) {
    Game.NTE -> R.drawable.bg_character_nte
    Game.WARFRAME -> R.drawable.bg_page_warframe
    Game.ENDFIELD -> R.drawable.bg_page_endfield
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
    terms: AppTerms,
    padding: PaddingValues
) {
    LazyColumn(
        Modifier.fillMaxSize().padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                "${terms.text("character.list.titlePrefix", "Personagens")} — ${terms.gameName(game)}",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(terms.text("character.search.label", "Buscar personagem")) },
                placeholder = {
                    Text(terms.text("character.search.placeholder", "Nome, função ou elemento"))
                }
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
                            Text(terms.text("character.filters.clear", "Limpar filtros"))
                        }
                    }
                }
            }
        }

        if (characters.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            terms.text("character.empty.title", "Nenhum personagem encontrado"),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            terms.text(
                                "character.empty.body",
                                "Tente buscar por outro nome, função ou elemento."
                            )
                        )
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
                            Button(onClick = { viewModel.selectCharacter(character) }) {
                                Text(terms.text("character.builds.button", "Ver builds"))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildScreen(
    character: GameCharacter,
    builds: List<CharacterBuild>,
    comparisonBuilds: List<CharacterBuild>,
    buildTypes: List<BuildType>,
    selectedBuildType: BuildType?,
    onBuildTypeChange: (BuildType?) -> Unit,
    terms: AppTerms,
    padding: PaddingValues
) {
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

        if (buildTypes.size > 1) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        terms.text("build.filters.title", "Filtrar builds"),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedBuildType == null,
                            onClick = { onBuildTypeChange(null) },
                            label = { Text(terms.text("build.filters.all", "Todas")) }
                        )
                        buildTypes.forEach { type ->
                            FilterChip(
                                selected = selectedBuildType == type,
                                onClick = { onBuildTypeChange(type) },
                                label = { Text(terms.buildTypeName(type)) }
                            )
                        }
                    }
                }
            }
        }

        if (comparisonBuilds.size > 1) {
            item { BuildComparisonCard(comparisonBuilds, terms) }
        }

        if (builds.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            terms.text("build.empty.title", "Sem builds disponíveis"),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            terms.text(
                                "build.empty.body",
                                "Os dados desta personagem ainda não foram adicionados à V0.3."
                            )
                        )
                    }
                }
            }
        } else {
            items(builds, key = { it.id }) { build -> BuildCard(build, terms) }
        }
    }
}

@Composable
private fun BuildComparisonCard(builds: List<CharacterBuild>, terms: AppTerms) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    terms.text("build.compare.title", "Comparação rápida"),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    terms.text("build.compare.body", "Principais diferenças entre as opções disponíveis."),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            builds.forEachIndexed { index, build ->
                if (index > 0) HorizontalDivider()
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        "${terms.buildTypeName(build.type)} — ${build.title}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    BuildComparisonLine(
                        terms.text("build.section.weapon", "Arma"),
                        build.weapon
                    )
                    BuildComparisonLine(
                        terms.text("build.section.equipment", "Equipamentos"),
                        build.equipment.joinToString(" • ").ifBlank { "—" }
                    )
                    BuildComparisonLine(
                        terms.text("build.section.stats", "Prioridade de stats"),
                        build.statPriority.joinToString(" > ").ifBlank { "—" }
                    )
                    BuildComparisonLine(
                        terms.text("build.section.team", "Equipe"),
                        build.team.joinToString(" • ").ifBlank { "—" }
                    )
                }
            }
        }
    }
}

@Composable
private fun BuildComparisonLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Text(value, style = MaterialTheme.typography.bodyMedium)
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
private fun BuildCard(build: CharacterBuild, terms: AppTerms) {
    val context = LocalContext.current
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(build.title, style = MaterialTheme.typography.titleLarge)
            Text(
                "${terms.buildTypeName(build.type)} • ${build.version}",
                style = MaterialTheme.typography.labelLarge
            )
            HorizontalDivider()
            BuildVisualSection(
                title = terms.text("build.section.weapon", "Arma"),
                values = listOf(build.weapon),
                imageUrls = listOf(build.weaponImageUrl ?: buildItemImageUrl(build.weapon)),
                terms = terms
            )
            BuildVisualSection(
                title = terms.text("build.section.equipment", "Equipamentos"),
                values = build.equipment,
                imageUrls = build.equipment.mapIndexed { index, value ->
                    build.equipmentImageUrls.getOrNull(index) ?: buildItemImageUrl(value)
                },
                terms = terms
            )
            BuildSection(terms.text("build.section.stats", "Prioridade de stats"), build.statPriority)
            BuildSection(terms.text("build.section.team", "Equipe"), build.team)
            HorizontalDivider()
            Text(terms.text("build.section.notes", "Notas"), style = MaterialTheme.typography.titleMedium)
            Text(build.notes)

            if (build.sources.isNotEmpty()) {
                HorizontalDivider()
                Text(
                    terms.text("build.section.sources", "Fontes"),
                    style = MaterialTheme.typography.titleMedium
                )
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

private data class BuildItemDetails(
    val name: String,
    val attributes: List<String>
)

@Composable
private fun BuildVisualSection(
    title: String,
    values: List<String>,
    imageUrls: List<String?>,
    terms: AppTerms
) {
    val selectedItem = remember { mutableStateOf<BuildItemDetails?>(null) }

    Text(title, style = MaterialTheme.typography.titleMedium)
    values.forEachIndexed { index, value ->
        val imageUrl = imageUrls.getOrNull(index)
        val details = buildItemDetails(value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (details != null) {
                        Modifier.clickable { selectedItem.value = details }
                    } else {
                        Modifier
                    }
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Imagem de $value",
                        modifier = Modifier.fillMaxSize().padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(value.take(1), style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
            Column(Modifier.width(230.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(value)
                if (details != null) {
                    Text(
                        terms.text("build.item.details.hint", "Toque para ver atributos"),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

    selectedItem.value?.let { details ->
        AlertDialog(
            onDismissRequest = { selectedItem.value = null },
            confirmButton = {
                TextButton(onClick = { selectedItem.value = null }) {
                    Text(terms.text("build.item.details.close", "Fechar"))
                }
            },
            title = { Text(details.name) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        terms.text("build.item.details.title", "Atributos especiais"),
                        style = MaterialTheme.typography.titleMedium
                    )
                    details.attributes.forEach { attribute -> Text("• $attribute") }
                }
            }
        )
    }
}

private fun buildItemDetails(value: String): BuildItemDetails? {
    val normalized = value.lowercase()
    return when {
        "speedy hedgehog" in normalized -> BuildItemDetails(
            name = value,
            attributes = listOf(
                "Melhora a geração de Ultimate.",
                "Adiciona suporte de ATK para a equipe."
            )
        )
        "diabolos" in normalized -> BuildItemDetails(
            name = value,
            attributes = listOf(
                "Concede bônus de Chaos DMG.",
                "Oferece Chaos RES Ignore."
            )
        )
        "kingdom's guard" in normalized -> BuildItemDetails(
            name = value,
            attributes = listOf(
                "Prioriza DEF para o usuário.",
                "Aumenta a potência dos escudos."
            )
        )
        else -> null
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

private fun AppTerms.gameName(game: Game): String = when (game) {
    Game.NTE -> text("game.nte.name", game.displayName)
    Game.WARFRAME -> text("game.warframe.name", game.displayName)
    Game.ENDFIELD -> text("game.endfield.name", game.displayName)
}

private fun AppTerms.gameDescription(game: Game): String = when (game) {
    Game.NTE -> text("game.nte.description", game.description)
    Game.WARFRAME -> text("game.warframe.description", game.description)
    Game.ENDFIELD -> text("game.endfield.description", game.description)
}

private fun AppTerms.buildTypeName(type: BuildType): String = when (type) {
    BuildType.META -> text("build.type.meta", type.displayName)
    BuildType.F2P -> text("build.type.f2p", type.displayName)
}
