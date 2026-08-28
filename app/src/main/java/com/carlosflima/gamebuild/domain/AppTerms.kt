package com.carlosflima.gamebuild.domain

data class AppTerms(
    val values: Map<String, String> = emptyMap()
) {
    fun text(key: String, fallback: String): String =
        values[key]?.takeIf { it.isNotBlank() } ?: fallback

    fun mergedWith(overrides: AppTerms): AppTerms =
        AppTerms(values + overrides.values)

    companion object {
        val Empty = AppTerms()
    }
}
