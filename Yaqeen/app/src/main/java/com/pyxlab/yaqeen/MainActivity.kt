package com.pyxlab.yaqeen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pyxlab.yaqeen.ui.screen.InputScreen
import com.pyxlab.yaqeen.ui.screen.WebViewScreen
import com.pyxlab.yaqeen.ui.theme.YaqeenTheme
import com.pyxlab.yaqeen.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YaqeenTheme {
                val url by viewModel.url.collectAsState()
                if (url != null) {
                    WebViewScreen(url = url!!)
                } else {
                    InputScreen(onConnect = viewModel::saveAndConnect)
                }
            }
        }
    }
}
