package com.carlosflima.gamebuild.ui

internal data class BuildItemDetails(
    val name: String,
    val attributes: List<String>
)

internal fun buildItemDetails(value: String): BuildItemDetails? {
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

internal fun buildItemImageUrl(value: String): String? {
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
