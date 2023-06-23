package com.example.storyapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.local.preference.AuthPreferences
import com.example.storyapp.databinding.ActivityAuthenticationBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory
import com.example.storyapp.ui.fragments.LoginFragment

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = AuthPreferences.getInstance(this.dataStore)
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        authViewModel.getToken().observe(this) { token ->
            if (token != "") {
                isLogin(token)
            } else {
                if (fragment !is LoginFragment) {
                    fragmentManager
                        .beginTransaction()
                        .add(
                            R.id.auth_fragment_container,
                            loginFragment,
                            LoginFragment::class.java.simpleName
                        )
                        .commit()
                }
            }
        }
    }

    private fun isLogin(token: String) {
        if (token != "") {
            val intentToMain = Intent(this, MainActivity::class.java)
            startActivity(intentToMain)
            Toast.makeText(this, "Welcome to StoryApp!", Toast.LENGTH_SHORT).show()
        }
    }
}