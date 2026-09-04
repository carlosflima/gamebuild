package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.CharacterBuild

internal fun buildShareText(build: CharacterBuild, terms: AppTerms): String = buildString {
    appendLine(build.title)
    appendLine("${terms.buildTypeName(build.type)} • ${build.version}")
    appendLine()
    appendLine("${terms.text("build.section.weapon", "Arma")}: ${build.weapon}")
    appendLine(
        "${terms.text("build.section.equipment", "Equipamentos")}: " +
            build.equipment.joinToString(" • ").ifBlank { "—" }
    )
    appendLine(
        "${terms.text("build.section.stats", "Prioridade de stats")}: " +
            build.statPriority.joinToString(" > ").ifBlank { "—" }
    )
    appendLine(
        "${terms.text("build.section.team", "Equipe")}: " +
            build.team.joinToString(" • ").ifBlank { "—" }
    )

    if (build.notes.isNotBlank()) {
        appendLine()
        appendLine("${terms.text("build.section.notes", "Notas")}:")
        appendLine(build.notes)
    }

    if (build.sources.isNotEmpty()) {
        appendLine()
        appendLine("${terms.text("build.section.sources", "Fontes")}:")
        build.sources.forEach { source ->
            append("• ${source.name}")
            source.url?.let { url -> append(" — $url") }
            appendLine()
        }
    }
}.trim()
