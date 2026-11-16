package com.sllauncher.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sllauncher.app.home.HomeRoute
import com.sllauncher.app.setup.LauncherSetupRoute
import kotlinx.serialization.Serializable

/**
 * Type-safe destinations. Each later phase adds its own route here
 * (app drawer, search, settings, ...). Floating panels such as the app
 * drawer will be overlays on Home rather than full-screen destinations.
 */
@Serializable
data object HomeDestination

@Serializable
data object LauncherSetupDestination

@Composable
fun SLNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        composable<HomeDestination> {
            HomeRoute(
                onOpenLauncherSetup = {
                    navController.navigate(LauncherSetupDestination) { launchSingleTop = true }
                },
            )
        }
        composable<LauncherSetupDestination> {
            LauncherSetupRoute(onDone = { navController.popBackStack(HomeDestination, inclusive = false) })
        }
    }
}

/** Returns to the home screen, closing anything above it. Used when Home is pressed. */
fun NavHostController.returnToHome() {
    popBackStack(HomeDestination, inclusive = false)
}
