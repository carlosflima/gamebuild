package com.carlosflima.gamebuild.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BuildItemCatalogRegressionTest {
    @Test
    fun imageCatalogKeepsKnownMappings() {
        val expected = mapOf(
            "Ready-Ready" to "https://cdn.prydwen.gg/images/nte/weapons/3.webp",
            "Raging Flames" to "https://cdn.prydwen.gg/images/nte/weapons/48.webp",
            "Good Boy's Grand Adventure" to "https://cdn.prydwen.gg/images/nte/weapons/36.webp",
            "Day Off" to "https://cdn.prydwen.gg/images/nte/weapons/13.webp",
            "Camellia Society" to "https://cdn.prydwen.gg/images/nte/weapons/5.webp",
            "Youthful Fantasy" to "https://cdn.prydwen.gg/images/nte/weapons/4.webp",
            "Blow Up the Crowd" to "https://cdn.prydwen.gg/images/nte/weapons/28.webp",
            "Umbrella" to "https://cdn.prydwen.gg/images/nte/weapons/10.webp",
            "Speedy Hedgehog" to "https://cdn.prydwen.gg/images/nte/sets/10.webp",
            "Lost Radiance" to "https://cdn.prydwen.gg/images/nte/sets/7.webp",
            "Crimson: Twin Butterflies" to "https://cdn.prydwen.gg/images/nte/sets/1.webp",
            "Diabolos" to "https://cdn.prydwen.gg/images/nte/sets/4.webp",
            "Kingdom's Guard" to "https://cdn.prydwen.gg/images/nte/sets/6.webp",
            "Fireflies and the Forest" to "https://cdn.prydwen.gg/images/nte/sets/5.webp",
            "Arc: Solid" to "https://cdn.prydwen.gg/images/nte/icons/arc_solid.webp",
            "Arc: Gas" to "https://cdn.prydwen.gg/images/nte/icons/arc_gas.webp",
            "Arc: Liquid" to "https://cdn.prydwen.gg/images/nte/icons/arc_liquid.webp",
            "Arc: Plasma" to "https://cdn.prydwen.gg/images/nte/icons/arc_plasma.webp",
            "Arc: Synthesis" to "https://cdn.prydwen.gg/images/nte/icons/arc_synthesis.webp"
        )

        expected.forEach { (name, url) ->
            assertEquals(name, url, imageUrlFor(name))
        }
    }

    @Test
    fun catalogMatchingIsCaseInsensitiveAndUnknownItemsUseFallback() {
        assertEquals(
            "https://cdn.prydwen.gg/images/nte/weapons/3.webp",
            imageUrlFor("READY-READY")
        )
        assertNotNull(detailsFor("SPEEDY HEDGEHOG"))
        assertNull(imageUrlFor("Item desconhecido"))
        assertNull(detailsFor("Item desconhecido"))
    }

    @Test
    fun specialDetailsKeepExistingContent() {
        assertEquals(
            ItemDetails(
                name = "Speedy Hedgehog",
                attributes = listOf(
                    "Melhora a geração de Ultimate.",
                    "Adiciona suporte de ATK para a equipe."
                )
            ),
            detailsFor("Speedy Hedgehog")
        )
        assertEquals(
            ItemDetails(
                name = "Diabolos",
                attributes = listOf(
                    "Concede bônus de Chaos DMG.",
                    "Oferece Chaos RES Ignore."
                )
            ),
            detailsFor("Diabolos")
        )
        assertEquals(
            ItemDetails(
                name = "Kingdom's Guard",
                attributes = listOf(
                    "Prioriza DEF para o usuário.",
                    "Aumenta a potência dos escudos."
                )
            ),
            detailsFor("Kingdom's Guard")
        )
    }

    private fun imageUrlFor(value: String): String? {
        val method = appClass.getDeclaredMethod("buildItemImageUrl", String::class.java).apply {
            isAccessible = true
        }
        return method.invoke(null, value) as String?
    }

    private fun detailsFor(value: String): ItemDetails? {
        val method = appClass.getDeclaredMethod("buildItemDetails", String::class.java).apply {
            isAccessible = true
        }
        val result = method.invoke(null, value) ?: return null
        val nameMethod = result.javaClass.getDeclaredMethod("getName").apply { isAccessible = true }
        val attributesMethod = result.javaClass.getDeclaredMethod("getAttributes").apply { isAccessible = true }

        @Suppress("UNCHECKED_CAST")
        return ItemDetails(
            name = nameMethod.invoke(result) as String,
            attributes = attributesMethod.invoke(result) as List<String>
        )
    }

    private data class ItemDetails(
        val name: String,
        val attributes: List<String>
    )

    private companion object {
        val appClass: Class<*> = Class.forName("com.carlosflima.gamebuild.ui.GameBuildAppKt")
    }
}
