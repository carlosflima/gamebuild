package com.carlosflima.gamebuild.data

import org.json.JSONObject
import java.io.File

internal fun endfieldCatalogJson(): String =
    listOf(File("../config/endfield-builds-v1.json"), File("config/endfield-builds-v1.json"))
        .first { it.isFile }.readText(Charsets.UTF_8)

internal fun changedEndfieldCatalog(revision: Int, title: String = "Updated starter"): String {
    val document = JSONObject(endfieldCatalogJson())
    document.put("revision", revision)
    document.getJSONArray("builds").getJSONObject(0).put("title", title)
    return document.toString()
}
