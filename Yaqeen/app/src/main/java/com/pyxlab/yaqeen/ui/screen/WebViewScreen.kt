package com.pyxlab.yaqeen.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.SslErrorHandler
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var canGoBack by remember { mutableStateOf(false) }

    var fileChooserCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        fileChooserCallback?.onReceiveValue(if (uri != null) arrayOf(uri) else null)
        fileChooserCallback = null
    }

    var permissionRequest by remember { mutableStateOf<PermissionRequest?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val request = permissionRequest ?: return@rememberLauncherForActivityResult
        val grantedResources = mutableListOf<String>()
        for (resource in request.resources) {
            val neededPerms = mapWebViewResourceToAndroidPermissions(resource)
            if (neededPerms.isEmpty() || neededPerms.all { results[it] == true }) {
                grantedResources.add(resource)
            }
        }
        if (grantedResources.isNotEmpty()) {
            request.grant(grantedResources.toTypedArray())
        } else {
            request.deny()
        }
        permissionRequest = null
    }

    LaunchedEffect(permissionRequest) {
        permissionRequest?.let { req ->
            val allPerms = req.resources
                .flatMap { mapWebViewResourceToAndroidPermissions(it) }
                .distinct()
                .toTypedArray()
            if (allPerms.isNotEmpty()) {
                permissionLauncher.launch(allPerms)
            } else {
                req.grant(req.resources)
                permissionRequest = null
            }
        }
    }

    var geolocationOrigin by remember { mutableStateOf<String?>(null) }
    var geolocationCallback by remember { mutableStateOf<GeolocationPermissions.Callback?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val origin = geolocationOrigin
        val callback = geolocationCallback
        if (origin != null && callback != null) {
            callback.invoke(origin, granted, false)
        }
        geolocationOrigin = null
        geolocationCallback = null
    }

    val chromeClient = remember {
        object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileChooserCallback = filePathCallback
                filePickerLauncher.launch("*/*")
                return true
            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                permissionRequest = request
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                geolocationOrigin = origin
                geolocationCallback = callback
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    BackHandler(enabled = canGoBack) {
        webView?.goBack()
    }

    Box(modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars)) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    configureSettings()
                    webViewClient = createWebViewClient(
                        onLoadingChange = { loading, backEnabled ->
                            isLoading = loading
                            canGoBack = backEnabled
                        }
                    )
                    webChromeClient = chromeClient
                    loadUrl(url)
                }
            },
            update = { view -> webView = view },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

private fun WebView.configureSettings() {
    settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        useWideViewPort = true
        loadWithOverviewMode = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        allowFileAccess = true
        allowContentAccess = true
        mediaPlaybackRequiresUserGesture = false
    }
}

private fun createWebViewClient(
    onLoadingChange: (Boolean, Boolean) -> Unit
): WebViewClient {
    return object : WebViewClient() {
        override fun onReceivedSslError(
            view: WebView,
            handler: SslErrorHandler,
            error: SslError
        ) {
            handler.proceed()
        }

        override fun shouldOverrideUrlLoading(
            view: WebView,
            request: WebResourceRequest
        ): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            onLoadingChange(false, view.canGoBack())
        }
    }
}

private fun mapWebViewResourceToAndroidPermissions(resource: String): List<String> {
    return when (resource) {
        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> listOf(Manifest.permission.CAMERA)
        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> listOf(Manifest.permission.RECORD_AUDIO)
        else -> emptyList()
    }
}
