package com.example.storyapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.models.AuthViewModel
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.MainActivity
import com.example.storyapp.models.AuthViewModelFactory


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Password Validation
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.apply {
                    if (s.toString().length < 8) {
                        errorPass.visibility = View.VISIBLE
                        edLoginPassword.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_error_rounded)
                        })
                    } else {
                        errorPass.visibility = View.GONE
                        edLoginPassword.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_rounded)
                        })
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}

        })

        //Email Validation
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.apply {
                    if (isValidEmail(s.toString())) {
                        errorEmail.visibility = View.GONE
                        edLoginEmail.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_rounded)
                        })
                    } else {
                        errorEmail.visibility = View.VISIBLE
                        edLoginEmail.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_error_rounded)
                        })
                    }
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.btnLogin.setOnClickListener(this)
        binding.includeIntentRegister.registerIntent.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val pref = AuthPreferences.getInstance(requireContext().dataStore)
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]

        when (v.id) {
            R.id.registerIntent -> {
                val registerFragment = RegisterFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().apply {
                    replace(
                        R.id.auth_fragment_container,
                        registerFragment,
                        RegisterFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }
            R.id.btn_login -> {
                val email = binding.edLoginEmail.text.toString()
                val password = binding.edLoginPassword.text.toString()
                binding.apply {
                    val validInput = validationInput(email, password)
                    Log.d("btnLogin", "start!")
                    if (validInput) {
                        authViewModel.apply {
                            Log.d("btnLogin", "loading false!")
                            this.login(email, password)
                            this.isLoading.observe(requireActivity()) {
                                Log.d("btnLogin", "loading true!")
                                showLoading(it)
                            }
                            this.getToken().observe(requireActivity()) { token ->
                                Log.d("btnLogin", "get token!")
                                isLogin(token)
                            }
                        this.msg.observe(requireActivity()) { msg ->
                            if (msg == "success") {
                                Toast.makeText(requireContext(), "Welcome to StoryApp!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            }
                        }
                        }
                    }

                }
            }
        }
    }

    private fun isLogin(token: String) {
        if (token != "") {
            val intentToMain = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intentToMain)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.includeLoading.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun validationInput(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun isValidEmail(email: String): Boolean {
        val regexEmail = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return regexEmail.matches(email)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
