package com.dimalahmad.notificationcounterlikedislike;

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotifReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs: SharedPreferences = context.getSharedPreferences("CounterPrefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        when (intent.action) {
            "ACTION_LIKE" -> {
                val likeCount = prefs.getInt("LIKE_COUNT", 0) + 1
                editor.putInt("LIKE_COUNT", likeCount)
            }
            "ACTION_DISLIKE" -> {
                val dislikeCount = prefs.getInt("DISLIKE_COUNT", 0) + 1
                editor.putInt("DISLIKE_COUNT", dislikeCount)
            }
        }

        editor.apply()

        // Kirim broadcast lokal untuk memperbarui UI
        val updateIntent = Intent("ACTION_UPDATE_COUNTERS")
        LocalBroadcastManager.getInstance(context).sendBroadcast(updateIntent)
    }
}
