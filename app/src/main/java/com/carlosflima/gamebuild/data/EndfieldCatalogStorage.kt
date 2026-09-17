package com.carlosflima.gamebuild.data

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import javax.net.ssl.HttpsURLConnection

internal interface EndfieldCatalogCache {
    fun read(): String?
    fun write(json: String)
}

internal class FileEndfieldCatalogCache(private val file: File) : EndfieldCatalogCache {
    override fun read(): String? {
        if (!file.isFile || file.length() !in 1..EndfieldCatalogParser.MAX_DOCUMENT_BYTES.toLong()) {
            return null
        }
        return file.inputStream().use(::readEndfieldDocument)
    }

    override fun write(json: String) {
        require(json.toByteArray(Charsets.UTF_8).size in 1..EndfieldCatalogParser.MAX_DOCUMENT_BYTES)
        val temporary = File(file.parentFile, "${file.name}.tmp")
        try {
            temporary.outputStream().use { output ->
                output.write(json.toByteArray(Charsets.UTF_8))
                output.fd.sync()
            }
            Files.move(
                temporary.toPath(),
                file.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            )
        } finally {
            temporary.delete()
        }
    }
}

internal object EndfieldCatalogDownload {
    private const val REMOTE_URL =
        "https://raw.githubusercontent.com/carlosflima/gamebuild/main/config/endfield-builds-v1.json"

    // Called on Dispatchers.IO by RemoteEndfieldRepository.
    fun fetch(): String {
        val connection = URL("$REMOTE_URL?ts=${System.currentTimeMillis()}")
            .openConnection() as HttpsURLConnection
        return try {
            connection.requestMethod = "GET"
            connection.instanceFollowRedirects = false
            connection.useCaches = false
            connection.connectTimeout = 5_000
            connection.readTimeout = 5_000
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty("Cache-Control", "no-cache")
            connection.setRequestProperty("User-Agent", "Game-Builds-Android")
            require(connection.responseCode == 200)
            val size = connection.contentLengthLong
            require(size == -1L || size in 1..EndfieldCatalogParser.MAX_DOCUMENT_BYTES.toLong())
            connection.inputStream.use(::readEndfieldDocument)
        } finally {
            connection.disconnect()
        }
    }
}

internal fun readEndfieldDocument(input: InputStream): String {
    val output = ByteArrayOutputStream()
    val buffer = ByteArray(4_096)
    while (true) {
        val count = input.read(buffer)
        if (count < 0) break
        require(output.size() + count <= EndfieldCatalogParser.MAX_DOCUMENT_BYTES)
        output.write(buffer, 0, count)
    }
    return output.toString(Charsets.UTF_8.name())
}
