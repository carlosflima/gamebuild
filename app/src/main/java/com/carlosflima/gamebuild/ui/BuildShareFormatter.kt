package com.carlosflima.gamebuild.ui

import com.carlosflima.gamebuild.domain.AppTerms
import com.carlosflima.gamebuild.domain.BuildType
import com.carlosflima.gamebuild.domain.CharacterBuild

internal fun buildShareText(build: CharacterBuild, terms: AppTerms): String = buildString {
    val buildTypeName = when (build.type) {
        BuildType.META -> terms.text("build.type.meta", build.type.displayName)
        BuildType.F2P -> terms.text("build.type.f2p", build.type.displayName)
    }

    appendLine(build.title)
    appendLine("$buildTypeName • ${build.version}")
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
