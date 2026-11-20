package com.sllauncher.app.ui

import androidx.compose.runtime.Composable
import com.sllauncher.app.navigation.FeatureGraph
import com.sllauncher.app.navigation.HomeExtras
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Connects feature areas to the launcher shell: their screens
 * ([featureGraph]), their pieces of the home screen ([homeExtras]) and
 * app-wide overlays ([Overlays], e.g. the assistant panel).
 */
@Singleton
class AppShell @Inject constructor() {
    val featureGraph: FeatureGraph = { }

    fun homeExtras(): HomeExtras = HomeExtras()

    @Composable
    fun Overlays() = Unit
}
