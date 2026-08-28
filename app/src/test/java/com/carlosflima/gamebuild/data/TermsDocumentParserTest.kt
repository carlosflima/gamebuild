package com.carlosflima.gamebuild.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
import org.junit.Test

class TermsDocumentParserTest {
    @Test
    fun `accepts valid catalog`() {
        val parsed = TermsDocumentParser.parse(
            """
            {
              "schemaVersion": 1,
              "terms": {
                "app.title": "Game Builds",
                "game.select.title": "Escolha um jogo"
              }
            }
            """.trimIndent()
        )

        assertNotNull(parsed)
        assertEquals("Game Builds", parsed?.values?.get("app.title"))
    }

    @Test
    fun `rejects unsupported schema`() {
        val parsed = TermsDocumentParser.parse(
            """{"schemaVersion":2,"terms":{"app.title":"Game Builds"}}"""
        )

        assertNull(parsed)
    }

    @Test
    fun `rejects invalid key`() {
        val parsed = TermsDocumentParser.parse(
            """{"schemaVersion":1,"terms":{"../unsafe":"value"}}"""
        )

        assertNull(parsed)
    }

    @Test
    fun `rejects oversized value`() {
        val oversized = "x".repeat(513)
        val parsed = TermsDocumentParser.parse(
            """{"schemaVersion":1,"terms":{"app.title":"$oversized"}}"""
        )

        assertNull(parsed)
    }

    @Test
    fun `rejects control characters`() {
        val parsed = TermsDocumentParser.parse(
            """{"schemaVersion":1,"terms":{"app.title":"Game\u0001Builds"}}"""
        )

        assertNull(parsed)
    }
}
