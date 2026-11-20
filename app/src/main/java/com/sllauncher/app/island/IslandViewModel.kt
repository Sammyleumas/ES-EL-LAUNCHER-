package com.sllauncher.app.island

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sllauncher.app.data.apps.AppKey
import com.sllauncher.app.data.apps.AppRepository
import com.sllauncher.app.data.settings.IslandPosition
import com.sllauncher.app.data.settings.SettingsRepository
import com.sllauncher.app.device.BatteryRepository
import com.sllauncher.app.media.MediaSessionRepository
import com.sllauncher.app.notifications.NotificationRepository
import com.sllauncher.app.notifications.ReplyResult
import com.sllauncher.app.notifications.SLNotification
import com.sllauncher.app.notifications.SLNotificationAction
import com.sllauncher.app.productivity.FocusTimer
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class ReplyStatus { Idle, Sending, Sent, Failed, NotSupported }

data class IslandUiState(
    val enabled: Boolean = true,
    val hasNotificationAccess: Boolean = false,
    val activities: List<IslandActivity> = emptyList(),
    val pinnedId: String? = null,
    val hideMessageContent: Boolean = false,
    val position: IslandPosition = IslandPosition.Center,
    val replyStatus: ReplyStatus = ReplyStatus.Idle,
)

@HiltViewModel
class IslandViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val notifications: NotificationRepository,
    val media: MediaSessionRepository,
    battery: BatteryRepository,
    private val focus: FocusTimer,
    settings: SettingsRepository,
    private val apps: AppRepository,
) : ViewModel() {

    private val pinned = MutableStateFlow<String?>(null)
    private val dismissed = MutableStateFlow<Set<String>>(emptySet())
    private val replyStatus = MutableStateFlow(ReplyStatus.Idle)

    val uiState: StateFlow<IslandUiState> = combine(
        combine(notifications.notifications, notifications.connected) { n, c -> n to c },
        media.nowPlaying,
        battery.state.onStart<com.sllauncher.app.device.BatteryState?> { emit(null) },
        combine(focus.session, settings.settings) { f, s -> f to s },
        combine(pinned, dismissed, replyStatus) { p, d, r -> Triple(p, d, r) },
    ) { (list, connected), playing, batt, (session, s), (pin, gone, reply) ->
        IslandUiState(
            enabled = s.islandEnabled,
            hasNotificationAccess = connected || notifications.hasAccess(),
            activities = buildIslandActivities(
                notifications = list,
                nowPlaying = playing,
                battery = batt,
                focus = session,
                prefs = IslandPreferences(s.islandShowMusic, s.islandShowMessages, s.islandShowCharging, s.mutedNotificationPackages),
                pinnedId = pin,
                dismissedIds = gone,
                ownPackage = context.packageName,
            ),
            pinnedId = pin,
            hideMessageContent = s.islandHideMessageContent || s.hideNotificationContent,
            position = s.islandPosition,
            replyStatus = reply,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), IslandUiState())

    fun togglePin(activity: IslandActivity) {
        pinned.value = if (pinned.value == activity.id) null else activity.id
    }

    /** Dismisses a notification-based activity everywhere, or hides others until they change. */
    fun dismiss(activity: IslandActivity) {
        val key = when (activity) {
            is IslandActivity.Message -> activity.notification.key.takeIf { activity.notification.isClearable }
            is IslandActivity.Progress -> activity.notification.key.takeIf { activity.notification.isClearable }
            else -> null
        }
        if (key != null) notifications.dismiss(key) else dismissed.value = dismissed.value + activity.id
        if (pinned.value == activity.id) pinned.value = null
    }

    fun open(notification: SLNotification) = notifications.open(notification)

    fun runAction(action: SLNotificationAction) = notifications.runAction(action)

    fun reply(notification: SLNotification, text: String) {
        if (text.isBlank()) return
        replyStatus.value = ReplyStatus.Sending
        replyStatus.value = when (notifications.reply(notification, text.trim())) {
            ReplyResult.Sent -> ReplyStatus.Sent
            ReplyResult.NotSupported -> ReplyStatus.NotSupported
            ReplyResult.Failed -> ReplyStatus.Failed
        }
    }

    fun resetReply() { replyStatus.value = ReplyStatus.Idle }

    fun stopFocus() = focus.stop()

    /** Launcher key for a notification's app, so the island can show its real icon. */
    fun appKeyFor(packageName: String): AppKey? =
        apps.apps.value.firstOrNull { it.key.packageName == packageName && !it.isWorkProfile }?.key
            ?: apps.apps.value.firstOrNull { it.key.packageName == packageName }?.key
}
