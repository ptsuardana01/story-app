package com.example.storyapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.data.local.AuthPreferences
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")
class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.edRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.apply {
                    if (s.toString().length < 8) {
                        errorPass.visibility = View.VISIBLE
                        edRegisterPassword.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_error_rounded)
                        })
                    } else {
                        errorPass.visibility = View.GONE
                        edRegisterPassword.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_rounded)
                        })
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.edRegisterEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.apply {
                    if (isValidEmail(s.toString())) {
                        errorEmail.visibility = View.GONE
                        edRegisterEmail.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_rounded)
                        })
                    } else {
                        errorEmail.visibility = View.VISIBLE
                        edRegisterEmail.setBackgroundDrawable(context?.let {
                            ContextCompat.getDrawable(
                                it, R.drawable.custom_input_error_rounded)
                        })
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.includeIntentLogin.registerIntent.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val pref = AuthPreferences.getInstance(requireContext().dataStore)
        val authViewModel = ViewModelProvider(this, AuthViewModelFactory(pref))[AuthViewModel::class.java]
        when (v.id) {
            R.id.registerIntent -> {
                val loginFragment = LoginFragment()
                val fragmentManager = parentFragmentManager
                fragmentManager.beginTransaction().apply {
                    replace(
                        R.id.auth_fragment_container,
                        loginFragment,
                        LoginFragment::class.java.simpleName
                    )
                    addToBackStack(null)
                    commit()
                }
            }
            R.id.btn_register -> {
                binding.apply {
                    val name = edRegisterName.text.toString()
                    val email = edRegisterEmail.text.toString()
                    val password = edRegisterPassword.text.toString()

                    val inputValid = validationInput(name, email, password)

                    if (inputValid) {
                        authViewModel.apply {
                            this.register(name, email, password)
                            this.isLoading.observe(requireActivity()) {
                                showLoading(it)
                            }
                            this.getToken().observe(requireActivity()) { token ->
                                isLogin(token)
                                Toast.makeText(requireContext(), "Welcome to StoryApp!", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.includeLoading.progressBar.visibility = View.VISIBLE
            else binding.includeLoading.progressBar.visibility = View.GONE
    }

    private fun validationInput(name: String, email: String, password: String): Boolean {
        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) return true

        return false
    }

    private fun isLogin(token: String) {
        if (token != "") {
            val intentToMain = Intent(requireActivity(), MainActivity::class.java)
            startActivity(intentToMain)
        }
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