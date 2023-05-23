package com.example.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

class SplashScreenActivity : AppCompatActivity() {

    private val scope = CoroutineScope(newSingleThreadContext("splash"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        scope.launch {
            goToNextActivity()
        }
    }

    private suspend fun goToNextActivity() {
        delay(3000)
        val intentSplash = Intent(this@SplashScreenActivity, AuthenticationActivity::class.java)
        startActivity(intentSplash)
        finish()
    }
}