package com.carlosflima.gamebuild.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class AppTermsTest {

    @Test
    fun `text uses fallback for missing blank or whitespace values`() {
        val terms = AppTerms(
            mapOf(
                "blank" to "",
                "whitespace" to "   "
            )
        )

        assertEquals("fallback", terms.text("missing", "fallback"))
        assertEquals("fallback", terms.text("blank", "fallback"))
        assertEquals("fallback", terms.text("whitespace", "fallback"))
    }

    @Test
    fun `text returns configured non blank value`() {
        val terms = AppTerms(mapOf("label" to "Configurado"))

        assertEquals("Configurado", terms.text("label", "fallback"))
    }

    @Test
    fun `mergedWith overrides matching keys and preserves base keys`() {
        val base = AppTerms(
            mapOf(
                "shared" to "Base",
                "overridden" to "Antes"
            )
        )
        val overrides = AppTerms(
            mapOf(
                "overridden" to "Depois",
                "new" to "Novo"
            )
        )

        val merged = base.mergedWith(overrides)

        assertEquals("Base", merged.text("shared", "fallback"))
        assertEquals("Depois", merged.text("overridden", "fallback"))
        assertEquals("Novo", merged.text("new", "fallback"))
    }
}
