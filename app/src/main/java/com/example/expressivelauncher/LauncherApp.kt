package com.example.expressivelauncher

import android.app.Application
import com.example.expressivelauncher.data.AppRepository
import com.example.expressivelauncher.data.IconThemeManager
import com.example.expressivelauncher.data.LayoutStore
import com.example.expressivelauncher.data.SettingsStore
import com.example.expressivelauncher.data.WidgetHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class LauncherApp : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    val settings by lazy { SettingsStore(this) }
    val layout by lazy { LayoutStore(this, appScope) }
    val repo by lazy { AppRepository(this, appScope, IconThemeManager(this), settings.state.value.iconTheme) }
    val widgets by lazy { WidgetHolder(this) }

    override fun onCreate() {
        super.onCreate()
        com.example.expressivelauncher.data.CrashLog.install(this)
        com.example.expressivelauncher.data.AppStyles.load(this)
        com.example.expressivelauncher.data.LockScreenPrefs.init(this)
        // Дублируем манифестный приёмник: на части прошивок USER_PRESENT доходит только к динамическим.
        runCatching {
            androidx.core.content.ContextCompat.registerReceiver(
                this,
                com.example.expressivelauncher.data.LockScreenReceiver(),
                android.content.IntentFilter(android.content.Intent.ACTION_USER_PRESENT),
                androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED,
            )
        }
    }
}
