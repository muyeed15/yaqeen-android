package com.pyxlab.yaqeen.config

import android.content.Context
import java.io.File

object AppConfig {

    private const val CONFIG_FILE = "yaqeen.properties"
    private const val KEY_URL = "URL"
    private const val KEY_HOST = "HOST"
    private const val KEY_PORT = "PORT"
    private const val KEY_SCHEME = "SCHEME"
    private const val KEY_PATH = "PATH"
    private const val DEFAULT_SCHEME = "https"

    fun readUrl(context: Context): String? {
        val props = readProperties(context)
        return buildUrl(props)
    }

    fun saveUrl(context: Context, url: String) {
        val file = File(context.filesDir, CONFIG_FILE)
        file.writeText("$KEY_URL=$url")
    }

    private fun readProperties(context: Context): Map<String, String> {
        val file = File(context.filesDir, CONFIG_FILE)
        if (file.exists()) {
            val parsed = parseProperties(file.readText())
            if (parsed.isNotEmpty()) {
                return parsed
            }
        }
        return try {
            val text = context.assets.open(CONFIG_FILE).bufferedReader().use { it.readText() }
            parseProperties(text)
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private fun parseProperties(text: String): Map<String, String> {
        val props = mutableMapOf<String, String>()
        text.lines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return@forEach
            }
            val eq = trimmed.indexOf('=')
            if (eq > 0) {
                val key = trimmed.substring(0, eq).trim()
                val value = trimmed.substring(eq + 1).trim()
                if (value.isNotEmpty()) {
                    props[key.uppercase()] = value
                }
            }
        }
        return props
    }

    private fun buildUrl(props: Map<String, String>): String? {
        props[KEY_URL]?.let { return it }
        val host = props[KEY_HOST] ?: return null
        val scheme = props[KEY_SCHEME] ?: DEFAULT_SCHEME
        val port = props[KEY_PORT]
        val path = props[KEY_PATH] ?: ""
        val base = "$scheme://$host"
        val withPort = if (port != null) "$base:$port" else base
        return if (path.startsWith("/") || path.isEmpty()) {
            "$withPort$path"
        } else {
            "$withPort/$path"
        }
    }
}
