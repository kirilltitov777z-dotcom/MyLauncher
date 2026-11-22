package com.example.expressivelauncher.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Настройки собственного экрана блокировки. Хранятся отдельно от основных настроек лаунчера,
 * чтобы не ломать их резервную копию и не раздувать [Settings].
 */
object LockScreenPrefs {
    data class State(
        val enabled: Boolean = true,
        val afterSystem: Boolean = true,
        val clock: Int = 0,
        val accent: Int = 0xFF31D07F.toInt(),
        val seconds: Boolean = false,
    )

    private const val NAME = "lock_screen"
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun init(context: Context) {
        val p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        _state.value = State(
            enabled = p.getBoolean("enabled", true),
            afterSystem = p.getBoolean("afterSystem", true),
            clock = p.getInt("clock", 0),
            accent = p.getInt("accent", 0xFF31D07F.toInt()),
            seconds = p.getBoolean("seconds", false),
        )
    }

    fun update(context: Context, transform: (State) -> State) {
        val new = transform(_state.value)
        _state.value = new
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putBoolean("enabled", new.enabled)
            .putBoolean("afterSystem", new.afterSystem)
            .putInt("clock", new.clock)
            .putInt("accent", new.accent)
            .putBoolean("seconds", new.seconds)
            .apply()
    }

    /** Система сообщила, что пользователь снял блокировку: ставим отложенный показ. */
    @Volatile
    private var lastPresent = 0L

    fun onUserPresent(context: Context) {
        val now = System.currentTimeMillis()
        if (now - lastPresent < 2500L) return // защита от дублей (манифест + динамическая регистрация)
        lastPresent = now
        val s = _state.value
        if (!s.enabled || !s.afterSystem) return
        context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit()
            .putBoolean("pending", true).apply()
        // Если лаунчер уже на переднем плане — onResume не придёт, пробуем показать сразу.
        com.example.expressivelauncher.LockScreenActivity.open(context)
    }

    fun consumePending(context: Context): Boolean {
        val p = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
        if (!p.getBoolean("pending", false)) return false
        p.edit().putBoolean("pending", false).apply()
        return true
    }
}
