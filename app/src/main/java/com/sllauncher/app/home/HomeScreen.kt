package com.sllauncher.app.home

import android.text.format.DateFormat
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.os.ConfigurationCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sllauncher.app.R
import com.sllauncher.core.designsystem.component.SLButton
import com.sllauncher.core.designsystem.theme.SLTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeRoute(
    onOpenLauncherSetup: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // The user may change the default launcher in Settings while we are paused.
    LifecycleResumeEffect(Unit) {
        viewModel.refreshDefaultHomeStatus()
        onPauseOrDispose { }
    }

    // When SL is the Home app, Back on the home screen must do nothing;
    // otherwise Android would close and immediately relaunch the launcher.
    BackHandler(enabled = state.isDefaultHome) { }

    HomeScreen(state = state, onOpenLauncherSetup = onOpenLauncherSetup)
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onOpenLauncherSetup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The window theme draws the system wallpaper behind this screen,
    // so the root intentionally has no background.
    Box(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(SLTheme.spacing.xl),
    ) {
        ClockBlock(now = state.now, modifier = Modifier.align(Alignment.TopStart))

        if (!state.isDefaultHome) {
            NotDefaultBanner(
                onSetUp = onOpenLauncherSetup,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            )
        }
    }
}

/** Shadow keeps white text readable on light wallpapers without a gradient scrim. */
private val WallpaperTextShadow = Shadow(color = Color(0x99000000), offset = Offset(0f, 2f), blurRadius = 6f)

@Composable
private fun ClockBlock(now: ZonedDateTime, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val locale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0] ?: java.util.Locale.getDefault()
    val is24Hour = DateFormat.is24HourFormat(context)

    val timeFormatter = remember(locale, is24Hour) {
        DateTimeFormatter.ofPattern(if (is24Hour) "HH:mm" else "h:mm", locale)
    }
    val dateFormatter = remember(locale) {
        DateTimeFormatter.ofPattern(DateFormat.getBestDateTimePattern(locale, "EEEEdMMMM"), locale)
    }

    Column(modifier) {
        Text(
            text = now.format(timeFormatter),
            style = SLTheme.typography.display.copy(shadow = WallpaperTextShadow),
            color = SLTheme.colors.textPrimary,
            modifier = Modifier.semantics { heading() },
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Yellow tick: the SL identity marker, used sparingly.
            Box(
                Modifier
                    .size(width = 16.dp, height = 4.dp)
                    .background(SLTheme.colors.accent),
            )
            Spacer(Modifier.size(SLTheme.spacing.sm))
            Text(
                text = now.format(dateFormatter).uppercase(locale),
                style = SLTheme.typography.label.copy(shadow = WallpaperTextShadow),
                color = SLTheme.colors.textPrimary,
            )
        }
    }
}

@Composable
private fun NotDefaultBanner(onSetUp: () -> Unit, modifier: Modifier = Modifier) {
    val colors = SLTheme.colors
    Row(
        modifier = modifier
            .background(colors.surface)
            .border(SLTheme.shapes.borderWidth, colors.border)
            .padding(SLTheme.spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SLTheme.spacing.md),
    ) {
        Text(
            text = stringResource(R.string.home_not_default_title),
            style = SLTheme.typography.body,
            color = colors.textPrimary,
            modifier = Modifier.weight(1f),
        )
        SLButton(text = stringResource(R.string.home_not_default_action), onClick = onSetUp)
    }
}
