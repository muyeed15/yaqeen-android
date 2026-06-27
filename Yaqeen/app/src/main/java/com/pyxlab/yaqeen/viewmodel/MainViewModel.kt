package com.pyxlab.yaqeen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pyxlab.yaqeen.config.AppConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val urlState = MutableStateFlow<String?>(null)
    val url: StateFlow<String?> = urlState.asStateFlow()

    init {
        urlState.value = AppConfig.readUrl(application)
    }

    fun saveAndConnect(input: String) {
        val url = normalizeUrl(input)
        urlState.value = url
        AppConfig.saveUrl(getApplication(), url)
    }

    private fun normalizeUrl(input: String): String {
        val trimmed = input.trim()
        return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
    }
}
