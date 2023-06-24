package com.example.storyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.data.local.preference.AuthPreferencesViewModel
import com.example.storyapp.databinding.ActivityAuthenticationBinding
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.ui.fragments.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding
    private val authPreferencesViewModel: AuthPreferencesViewModel by viewModels {
        AuthViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authPreferencesViewModel.getToken().observe(this) { token ->
            isLogin(token)
        }
    }

    private fun isLogin(token: String) {
        val fragmentManager = supportFragmentManager

        if (token != "") {
            val intentToMain = Intent(this@AuthenticationActivity, MainActivity::class.java)
            startActivity(intentToMain)
            Toast.makeText(this, R.string.welcome_text, Toast.LENGTH_SHORT).show()
        } else {
            fragmentManager
                .beginTransaction()
                .replace(
                    R.id.auth_fragment_container,
                    LoginFragment(),
                    LoginFragment::class.java.simpleName
                ).commit()
        }
    }
}