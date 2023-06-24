package com.example.storyapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.storyapp.MainActivity
import com.example.storyapp.R
import com.example.storyapp.data.local.preference.AuthPreferencesViewModel
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val authPreferencesViewModel: AuthPreferencesViewModel by activityViewModels {
        AuthViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferencesViewModel.getToken().observe(requireActivity()) { token ->
            isLogin(token)
        }

        binding.btnLogin.setOnClickListener(this)
        binding.includeIntentRegister.registerIntent.setOnClickListener(this)
    }

    override fun onClick(v: View) {
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
                    if (validInput) {
                        authViewModel.apply {
                            login(email, password)
                            isLoading.observe(requireActivity()) {
                                showLoading(it)
                            }
                            login.observe(requireActivity()) { token ->
                                authPreferencesViewModel.saveToken(token.token)
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
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            Toast.makeText(requireContext(), R.string.welcome_text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.includeLoading.progressBar.visibility = View.VISIBLE
        else binding.includeLoading.progressBar.visibility = View.GONE
    }

    private fun validationInput(email: String, password: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            if (binding.edLoginEmail.valid && binding.edLoginPassword.valid) {
                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
