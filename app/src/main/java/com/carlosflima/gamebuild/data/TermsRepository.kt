package com.carlosflima.gamebuild.data

import android.content.Context
import com.carlosflima.gamebuild.domain.AppTerms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class TermsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val cacheFile = File(appContext.filesDir, CACHE_FILE_NAME)

    fun loadInitial(): AppTerms {
        val fallback = loadBundledTerms()
        val cached = runCatching {
            if (cacheFile.exists()) parseTerms(cacheFile.readText()) else null
        }.getOrNull()

        return if (cached != null) fallback.mergedWith(cached) else fallback
    }

    suspend fun refreshFromRemote(): AppTerms? = withContext(Dispatchers.IO) {
        val fallback = loadBundledTerms()
        val remoteJson = runCatching { downloadRemoteTerms() }.getOrNull() ?: return@withContext null
        val remote = parseTerms(remoteJson) ?: return@withContext null

        runCatching { cacheFile.writeText(remoteJson) }
        fallback.mergedWith(remote)
    }

    private fun loadBundledTerms(): AppTerms = runCatching {
        appContext.assets.open(ASSET_FILE_NAME).bufferedReader().use { reader ->
            parseTerms(reader.readText()) ?: AppTerms.Empty
        }
    }.getOrDefault(AppTerms.Empty)

    private fun parseTerms(json: String): AppTerms? = runCatching {
        val root = JSONObject(json)
        if (root.optInt("schemaVersion", -1) != SUPPORTED_SCHEMA_VERSION) return null

        val termsObject = root.optJSONObject("terms") ?: return null
        val values = mutableMapOf<String, String>()
        val keys = termsObject.keys()

        while (keys.hasNext()) {
            val key = keys.next()
            val value = termsObject.optString(key, "").trim()
            if (key.isNotBlank() && value.isNotBlank()) values[key] = value
        }

        if (values.isEmpty()) null else AppTerms(values)
    }.getOrNull()

    private fun downloadRemoteTerms(): String {
        val cacheBuster = System.currentTimeMillis()
        val connection = URL("$REMOTE_URL?ts=$cacheBuster").openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("User-Agent", "Game-Builds-Android")
            connection.connect()

            if (connection.responseCode !in 200..299) {
                error("Falha ao carregar termos: HTTP ${connection.responseCode}")
            }

            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        private const val SUPPORTED_SCHEMA_VERSION = 1
        private const val ASSET_FILE_NAME = "terms.json"
        private const val CACHE_FILE_NAME = "terms-cache.json"
        private const val REMOTE_URL =
            "https://raw.githubusercontent.com/carlosflima/gamebuild/main/config/terms.json"
    }
}
