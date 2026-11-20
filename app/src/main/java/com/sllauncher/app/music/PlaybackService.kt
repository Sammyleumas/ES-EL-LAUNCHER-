package com.sllauncher.app.music

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.sllauncher.app.MainActivity

/** The audio session id of SL's player, for the system equalizer panel. */
object AudioSessionHolder {
    @Volatile var audioSessionId: Int = C.AUDIO_SESSION_ID_UNSET
}

/**
 * Plays local music with a system media session, so lock-screen, Bluetooth
 * and headset controls work and the Dynamic Island sees it like any player.
 * Runs as a media foreground service only while music is playing.
 */
class PlaybackService : MediaSessionService() {
    private var session: MediaSession? = null

    @OptIn(UnstableApi::class) // audioSessionId is needed for the system equalizer panel.
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_MUSIC).build(), true)
            .setHandleAudioBecomingNoisy(true) // Pause when headphones are unplugged.
            .build()
        AudioSessionHolder.audioSessionId = player.audioSessionId
        val open = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        session = MediaSession.Builder(this, player).setSessionActivity(open).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    /** Stop when the user swipes SL away and nothing is playing. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = session?.player
        if (player == null || !player.playWhenReady || player.mediaItemCount == 0) stopSelf()
    }

    override fun onDestroy() {
        session?.run {
            player.release()
            release()
        }
        session = null
        AudioSessionHolder.audioSessionId = C.AUDIO_SESSION_ID_UNSET
        super.onDestroy()
    }
}
