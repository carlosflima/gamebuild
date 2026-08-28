package com.carlosflima.gamebuild.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.Properties

object AppTermsRepository {
    private const val REMOTE_URL =
        "https://raw.githubusercontent.com/carlosflima/gamebuild/main/config/app-terms.properties"

    val defaults: Map<String, String> = mapOf(
        "app.title" to "Game Builds — V0.3",
        "game.select.title" to "Selecione um jogo",
        "game.select.button" to "Selecionar",
        "character.title" to "Personagens",
        "character.search.label" to "Buscar personagem",
        "character.search.placeholder" to "Nome, função ou elemento",
        "filters.clear" to "Limpar filtros",
        "character.empty.title" to "Nenhum personagem encontrado",
        "character.empty.body" to "Tente buscar por outro nome, função ou elemento.",
        "character.builds.button" to "Ver builds",
        "build.empty.title" to "Sem builds disponíveis",
        "build.empty.body" to "Os dados desta personagem ainda não foram adicionados à V0.3.",
        "build.weapon" to "Arma",
        "build.equipment" to "Equipamentos",
        "build.stats" to "Prioridade de stats",
        "build.team" to "Equipe",
        "build.notes" to "Notas",
        "build.sources" to "Fontes",
        "dialog.error" to "Erro",
        "action.back" to "Voltar"
    )

    suspend fun load(): Map<String, String> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = URL(REMOTE_URL).openConnection().apply {
                connectTimeout = 4_000
                readTimeout = 4_000
                useCaches = false
            }
            val properties = Properties()
            connection.getInputStream().use(properties::load)

            defaults + properties.stringPropertyNames().associateWith { key ->
                properties.getProperty(key).trim()
            }.filterValues { it.isNotEmpty() }
        }.getOrElse { defaults }
    }
}
