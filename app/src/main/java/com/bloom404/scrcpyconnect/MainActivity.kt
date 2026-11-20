package com.bloom404.scrcpyconnect

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bloom404.scrcpyconnect.ui.screens.MainScreen
import com.bloom404.scrcpyconnect.ui.theme.DeepObsidian
import com.bloom404.scrcpyconnect.ui.theme.ScrcpyConnectTheme
import com.bloom404.scrcpyconnect.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScrcpyConnectTheme {
                val viewModel: MainViewModel = viewModel()
                val lifecycleOwner = LocalLifecycleOwner.current

                // Refresh on ON_RESUME + start periodic 2s refresh
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            viewModel.refreshSystemChecks()
                            viewModel.startPeriodicRefresh()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                    color = DeepObsidian
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
