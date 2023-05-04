package com.example.storyapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.databinding.ActivityAuthenticationBinding
import com.example.storyapp.ui.fragments.LoginFragment

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentManager = supportFragmentManager
        val loginFragment = LoginFragment()
        val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

        if (fragment !is LoginFragment) {
            Log.d("MyFlexibleFragment", "Fragment Name :" + LoginFragment::class.java.simpleName)
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