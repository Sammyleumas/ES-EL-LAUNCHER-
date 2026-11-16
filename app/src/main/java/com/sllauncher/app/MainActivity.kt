package com.sllauncher.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.rememberNavController
import com.sllauncher.app.navigation.SLNavHost
import com.sllauncher.app.navigation.returnToHome
import com.sllauncher.core.designsystem.theme.SLTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * The launcher's single activity. Registered in the manifest for the
 * HOME category, so Android shows it when the user presses Home once
 * SL Launcher is selected as the default home app.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /** Fires when the user presses Home while the launcher is already running. */
    private val homeButtonPresses = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        // Light icons on a dark/wallpaper background in both system themes.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        setContent {
            SLTheme {
                val navController = rememberNavController()
                LaunchedEffect(navController) {
                    homeButtonPresses.collect { navController.returnToHome() }
                }
                SLNavHost(navController = navController)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_MAIN && intent.hasCategory(Intent.CATEGORY_HOME)) {
            homeButtonPresses.tryEmit(Unit)
        }
    }
}
