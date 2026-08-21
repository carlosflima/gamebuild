package com.carlosflima.gamebuild.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.carlosflima.gamebuild.domain.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameBuildApp(viewModel: GameBuildViewModel = viewModel()) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value
    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("GameBuild — V0.2.5") }) }) { padding ->
            when {
                state.selectedCharacter != null -> CharacterBuildScreen(state.selectedCharacter!!, state.selectedBuild, viewModel, padding)
                state.selectedGame != null -> CharacterSelection(state.selectedGame!!, state.characters, state.filters, viewModel, padding)
                else -> GameSelection(viewModel, padding)
            }
            state.errorMessage?.let { message -> AlertDialog(onDismissRequest = viewModel::clearError, confirmButton = { Button(onClick = viewModel::clearError) { Text("OK") } }, title = { Text("Erro") }, text = { Text(message) }) }
        }
    }
}

@Composable
private fun GameSelection(viewModel: GameBuildViewModel, padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Selecione um jogo", style = MaterialTheme.typography.headlineSmall)
        Game.entries.forEach { game -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { Text(game.displayName, style = MaterialTheme.typography.titleLarge); Text(game.description); Button(onClick = { viewModel.selectGame(game) }) { Text("Selecionar") } } } }
    }
}

@Composable
private fun CharacterSelection(game: Game, characters: List<GameCharacter>, filters: NteFilters, viewModel: GameBuildViewModel, padding: PaddingValues) {
    val filtered = characters.filter { c -> (filters.query.isBlank() || c.name.contains(filters.query, true)) && (filters.element == null || c.element.equals(filters.element, true)) && (filters.role == null || c.role.equals(filters.role, true)) }
    val elements = characters.mapNotNull { it.element }.distinct().sorted(); val roles = characters.map { it.role }.distinct().sorted()
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Text("Personagens — ${game.displayName}", style = MaterialTheme.typography.headlineSmall); OutlinedTextField(filters.query, viewModel::updateSearch, Modifier.fillMaxWidth().padding(top = 10.dp), label = { Text("Buscar personagem") }, singleLine = true, keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(imeAction = ImeAction.Search)) }
        if (elements.isNotEmpty()) item { Text("Elemento"); Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { elements.forEach { e -> FilterChip(filters.element == e, { viewModel.updateElement(if (filters.element == e) null else e) }, label = { Text(e) }) } } }
        if (roles.isNotEmpty()) item { Text("Função"); Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) { roles.forEach { r -> FilterChip(filters.role == r, { viewModel.updateRole(if (filters.role == r) null else r) }, label = { Text(r) }) } }; if (filters.query.isNotBlank() || filters.element != null || filters.role != null) AssistChip(onClick = viewModel::clearFilters, label = { Text("Limpar filtros") }) }
        item { Text("${filtered.size} personagem(ns)") }
        if (filtered.isEmpty()) item { Text("Nenhum personagem encontrado.") }
        items(filtered, key = { it.id }) { character -> Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) { CharacterImage(character); Column(Modifier.weight(1f)) { Text(character.name, style = MaterialTheme.typography.titleLarge); Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "?"}"); Text("${character.role} • ${character.arcType ?: "Arc não informado"} • Tier ${character.tier ?: "?"}") } }; Button(onClick = { viewModel.selectCharacter(character) }) { Text("Ver build") } } } }
    }
}

@Composable private fun CharacterImage(character: GameCharacter) { Card(Modifier.size(88.dp)) { if (!character.imageUrl.isNullOrBlank()) AsyncImage(character.imageUrl, "Imagem de ${character.name}", Modifier.fillMaxSize(), contentScale = ContentScale.Crop) else Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) { Text(character.name.take(1).uppercase(), Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineSmall); Text("Sem imagem", Modifier.fillMaxWidth(), textAlign = TextAlign.Center) } } }

@Composable
private fun CharacterBuildScreen(character: GameCharacter, build: CharacterBuild?, viewModel: GameBuildViewModel, padding: PaddingValues) {
    LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { Button(onClick = viewModel::clearSelection) { Text("Voltar") }; Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) { CharacterImage(character); Column { Text(character.name, style = MaterialTheme.typography.headlineMedium); Text("${character.rarity ?: "?"}-Rank • ${character.element ?: "?"}"); Text(character.role); Text("Tier: ${character.tier ?: "?"}") } } }
        if (build == null) item { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) { Text("Build em pesquisa", style = MaterialTheme.typography.titleLarge); Text("Ainda não há uma recomendação consolidada.") } } }
        else { item { BuildScoreCard(BuildScoreCalculator.calculate(build)) }; item { BuildOverviewCard(build) }; item { BuildEquipmentCard(build) }; item { BuildTeamCard(build) }; item { BuildSourcesCard(build) } }
    }
}

@Composable private fun BuildScoreCard(score: BuildScore) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) { Text("Build Score", style = MaterialTheme.typography.titleLarge); Text("${score.total}/100", style = MaterialTheme.typography.displaySmall); Text("Mede a qualidade da recomendação, não o poder absoluto.", style = MaterialTheme.typography.bodySmall); ScoreMetric("Fontes", score.sourceCoverage); ScoreMetric("Atualidade", score.freshness); ScoreMetric("Completude", score.buildCompleteness); ScoreMetric("F2P", score.f2pAvailability); ScoreMetric("Concordância", score.sourceAgreement) } } }
@Composable private fun ScoreMetric(label: String, value: Int) { Column { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(label); Text("$value%") }; LinearProgressIndicator(progress = { value / 100f }, Modifier.fillMaxWidth()) } }
@Composable private fun BuildOverviewCard(build: CharacterBuild) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text(build.title, style = MaterialTheme.typography.titleLarge); build.version?.let { Text("Versão: $it") }; build.sourceUpdatedAt?.let { Text("Atualizada: $it") }; build.arcRecommendation?.let { Text("Arc: $it") }; if (build.alternativeArcs.isNotEmpty()) Text("Alternativas: ${build.alternativeArcs.joinToString(" • ")}") } } }
@Composable private fun BuildEquipmentCard(build: CharacterBuild) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("Equipamentos e atributos", style = MaterialTheme.typography.titleMedium); build.cartridgeRecommendation?.let { Text("Cartridges: $it") }; if (build.modulePriority.isNotEmpty()) Text("Modules: ${build.modulePriority.joinToString(" → ")}"); if (build.statPriority.isNotEmpty()) Text("Stats: ${build.statPriority.joinToString(" → ")}"); if (build.skillPriority.isNotEmpty()) Text("Skills: ${build.skillPriority.joinToString(" → ")}") } } }
@Composable private fun BuildTeamCard(build: CharacterBuild) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("Equipes", style = MaterialTheme.typography.titleMedium); if (build.teamRecommendation.isNotEmpty()) Text("Recomendada: ${build.teamRecommendation.joinToString(" + ")}"); if (build.f2pTeam.isNotEmpty()) Text("F2P: ${build.f2pTeam.joinToString(" + ")}"); build.f2pNote?.let { Text(it, style = MaterialTheme.typography.bodySmall) } } } }
@Composable private fun BuildSourcesCard(build: CharacterBuild) { Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) { Text("Fontes", style = MaterialTheme.typography.titleMedium); build.sources.forEachIndexed { i, source -> Text("${i + 1}. $source", style = MaterialTheme.typography.bodySmall) } } } }
