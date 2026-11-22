package com.example.expressivelauncher

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.expressivelauncher.ui.lockscreen.LockScreen

class LockScreenActivity : ComponentActivity() {

    private val camera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Отмечаем показ: иначе при возврате в лаунчер onResume покажет экран второй раз
        com.example.expressivelauncher.data.LockScreenPrefs.consumePending(this)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val bars = WindowInsetsControllerCompat(window, window.decorView)
        bars.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        bars.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            LockScreen(
                onDismiss = { finish() },
                onCamera = { openCamera() },
            )
        }
    }

    private fun openCamera() {
        val capture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        val still = Intent(android.provider.MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
        val ok = runCatching {
            if (capture.resolveActivity(packageManager) != null) camera.launch(capture)
            else if (still.resolveActivity(packageManager) != null) startActivity(still)
            else error("no camera app")
        }.isSuccess
        if (!ok) Toast.makeText(this, "Камера недоступна", Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun open(context: Context) {
            runCatching {
                context.startActivity(
                    Intent(context, LockScreenActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }
}
