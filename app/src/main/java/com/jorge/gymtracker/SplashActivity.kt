package com.jorge.gymtracker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Aplica el tema del splash antes de super.onCreate para evitar parpadeos
        setTheme(R.style.Theme_GymTracker_Splash)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)

        // Abre la actividad principal tras un breve delay
        window.decorView.postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500) // 1.5 s
    }
}
