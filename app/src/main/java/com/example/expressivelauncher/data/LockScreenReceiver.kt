package com.example.expressivelauncher.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Показывает наш экран после того, как пользователь снял системную блокировку. */
class LockScreenReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return
        LockScreenPrefs.init(context)
        LockScreenPrefs.onUserPresent(context)
    }
}
