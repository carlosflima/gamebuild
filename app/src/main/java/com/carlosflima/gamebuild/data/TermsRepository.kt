package com.carlosflima.gamebuild.data

import android.content.Context
import com.carlosflima.gamebuild.domain.AppTerms
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

internal class TermsRepository(context: Context) {
    private val appContext = context.applicationContext
    private val cacheFile = File(appContext.filesDir, CACHE_FILE_NAME)

    fun loadInitial(): AppTerms {
        val fallback = loadBundledTerms()
        val cached = runCatching {
            if (cacheFile.exists() && cacheFile.length() in 1..MAX_DOCUMENT_BYTES.toLong()) {
                TermsDocumentParser.parse(cacheFile.readText(Charsets.UTF_8))
            } else {
                null
            }
        }.getOrNull()

        return if (cached != null) fallback.mergedWith(cached) else fallback
    }

    suspend fun refreshFromRemote(): AppTerms? = withContext(Dispatchers.IO) {
        val fallback = loadBundledTerms()
        val remoteJson = runCatching { downloadRemoteTerms() }.getOrNull() ?: return@withContext null
        val remote = TermsDocumentParser.parse(remoteJson) ?: return@withContext null

        runCatching { persistCacheAtomically(remoteJson) }
        fallback.mergedWith(remote)
    }

    private fun loadBundledTerms(): AppTerms = runCatching {
        appContext.assets.open(ASSET_FILE_NAME).bufferedReader(Charsets.UTF_8).use { reader ->
            TermsDocumentParser.parse(reader.readText()) ?: AppTerms.Empty
        }
    }.getOrDefault(AppTerms.Empty)

    private fun downloadRemoteTerms(): String {
        val cacheBuster = System.currentTimeMillis()
        val url = URL("$REMOTE_URL?ts=$cacheBuster")
        require(url.protocol == "https")
        require(url.host == REMOTE_HOST)
        require(url.path == REMOTE_PATH)

        val connection = url.openConnection() as HttpsURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = false
            connection.useCaches = false
            connection.connectTimeout = CONNECT_TIMEOUT_MS
            connection.readTimeout = READ_TIMEOUT_MS
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("User-Agent", "Game-Builds-Android")
            connection.connect()

            if (connection.responseCode !in 200..299) {
                error("Falha ao carregar termos: HTTP ${connection.responseCode}")
            }

            val contentLength = connection.contentLengthLong
            require(contentLength == -1L || contentLength in 1..MAX_DOCUMENT_BYTES.toLong())

            connection.inputStream.use(::readUtf8WithLimit)
        } finally {
            connection.disconnect()
        }
    }

    private fun readUtf8WithLimit(input: InputStream): String {
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(4_096)
        var total = 0

        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            total += read
            require(total <= MAX_DOCUMENT_BYTES)
            output.write(buffer, 0, read)
        }

        return output.toString(Charsets.UTF_8.name())
    }

    private fun persistCacheAtomically(json: String) {
        require(json.toByteArray(Charsets.UTF_8).size <= MAX_DOCUMENT_BYTES)
        val tempFile = File(appContext.filesDir, "$CACHE_FILE_NAME.tmp")
        tempFile.writeText(json, Charsets.UTF_8)

        if (!tempFile.renameTo(cacheFile)) {
            tempFile.copyTo(cacheFile, overwrite = true)
            tempFile.delete()
        }
    }

    private companion object {
        const val ASSET_FILE_NAME = "terms.json"
        const val CACHE_FILE_NAME = "terms-cache.json"
        const val REMOTE_HOST = "raw.githubusercontent.com"
        const val REMOTE_PATH = "/carlosflima/gamebuild/main/config/terms.json"
        const val REMOTE_URL = "https://$REMOTE_HOST$REMOTE_PATH"
        const val CONNECT_TIMEOUT_MS = 5_000
        const val READ_TIMEOUT_MS = 5_000
        const val MAX_DOCUMENT_BYTES = 65_536
    }
}

internal object TermsDocumentParser {
    private const val SUPPORTED_SCHEMA_VERSION = 1
    private const val MAX_TERM_ENTRIES = 128
    private const val MAX_TERM_VALUE_LENGTH = 512
    private const val MAX_DOCUMENT_BYTES = 65_536
    private val keyPattern = Regex("^[a-z0-9][a-z0-9._-]{0,63}$")

    fun parse(json: String): AppTerms? = runCatching {
        require(json.toByteArray(Charsets.UTF_8).size <= MAX_DOCUMENT_BYTES)
        val root = JSONObject(json)
        require(root.optInt("schemaVersion", -1) == SUPPORTED_SCHEMA_VERSION)

        val termsObject = requireNotNull(root.optJSONObject("terms"))
        require(termsObject.length() in 1..MAX_TERM_ENTRIES)

        val values = mutableMapOf<String, String>()
        val keys = termsObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            require(keyPattern.matches(key))

            val rawValue = termsObject.opt(key)
            require(rawValue is String)
            val value = rawValue.trim()
            require(value.isNotEmpty())
            require(value.length <= MAX_TERM_VALUE_LENGTH)
            require(value.none { char ->
                char == '\u0000' || (Character.isISOControl(char) && char != '\n' && char != '\t')
            })
            values[key] = value
        }

        require(values.isNotEmpty())
        AppTerms(values.toMap())
    }.getOrNull()
}
