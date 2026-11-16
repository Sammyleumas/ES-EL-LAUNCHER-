package com.sllauncher.app.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sllauncher.app.launcher.HomeRoleRepository
import com.sllauncher.app.launcher.TimeSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import java.time.ZonedDateTime
import javax.inject.Inject

data class HomeUiState(
    val now: ZonedDateTime,
    val isDefaultHome: Boolean,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRoleRepository: HomeRoleRepository,
    private val timeSource: TimeSource,
) : ViewModel() {

    /**
     * Emits once per minute, aligned to the start of each minute, so the clock
     * flips exactly when the minute changes without waking the CPU every second.
     * Stops 5 s after the home screen leaves the foreground (battery friendly).
     */
    private val minuteTicker = flow {
        while (true) {
            val now = timeSource.now()
            emit(now)
            delay(millisUntilNextMinute(now))
        }
    }

    val uiState: StateFlow<HomeUiState> = combine(
        minuteTicker,
        homeRoleRepository.isDefaultHome,
    ) { now, isDefault -> HomeUiState(now, isDefault) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = HomeUiState(timeSource.now(), homeRoleRepository.isDefaultHome.value),
        )

    fun refreshDefaultHomeStatus() = homeRoleRepository.refresh()

    internal companion object {
        fun millisUntilNextMinute(now: ZonedDateTime): Long {
            val elapsedInMinute = now.second * 1_000L + now.nano / 1_000_000L
            return 60_000L - elapsedInMinute
        }
    }
}
