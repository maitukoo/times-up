package com.oscar.timesup

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var web: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pantalla siempre encendida mientras se juega
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemBars()

        web = WebView(this).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true            // récords y palabras propias
            settings.mediaPlaybackRequiresUserGesture = false // sonidos WebAudio
            setBackgroundColor(0xFF14101F.toInt())
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            addJavascriptInterface(Bridge(), "Android")
            isHapticFeedbackEnabled = true
        }
        setContentView(web)
        if (savedInstanceState != null) web.restoreState(savedInstanceState)
        else web.loadUrl("file:///android_asset/index.html")

        // Botón atrás: si estás en partida vuelve al menú; si estás en el menú, sale.
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                web.evaluateJavascript("window.backPressed && window.backPressed()") { r ->
                    if (r != "true") finish()
                }
            }
        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemBars()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        web.saveState(outState)
    }

    override fun onPause() { super.onPause(); web.onPause() }
    override fun onResume() { super.onResume(); web.onResume() }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /** Puente JS → Android. El WebView no soporta navigator.vibrate, así que lo hacemos nativo. */
    inner class Bridge {
        /** Horizontal solo durante la partida; en el menú la orientación es libre. */
        @JavascriptInterface
        fun setLandscape(on: Boolean) {
            runOnUiThread {
                requestedOrientation = if (on) ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                       else ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }

        @JavascriptInterface
        fun vibrate(json: String) {
            val vib: Vibrator = if (Build.VERSION.SDK_INT >= 31) {
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            // Acepta un número (ms) o un patrón [vibra, pausa, vibra, ...] como navigator.vibrate
            val pattern: LongArray = try {
                val arr = JSONArray(json)
                LongArray(arr.length() + 1) { i -> if (i == 0) 0L else arr.getLong(i - 1) }
            } catch (e: Exception) {
                longArrayOf(0L, json.trim().toLongOrNull() ?: 50L)
            }
            if (Build.VERSION.SDK_INT >= 26) vib.vibrate(VibrationEffect.createWaveform(pattern, -1))
            else @Suppress("DEPRECATION") vib.vibrate(pattern, -1)
        }
    }
}
