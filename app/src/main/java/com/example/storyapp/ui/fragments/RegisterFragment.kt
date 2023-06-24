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
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.models.AuthViewModel
import com.example.storyapp.models.AuthViewModelFactory


class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by activityViewModels()
    private val authPreferencesViewModel: AuthPreferencesViewModel by activityViewModels {
        AuthViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authPreferencesViewModel.getToken().observe(requireActivity()) { token ->
            isRegister(token)
        }

        binding.includeIntentLogin.registerIntent.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
    }

    override fun onClick(v: View) {
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
                            register(name, email, password)
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

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) binding.includeLoading.progressBar.visibility = View.VISIBLE
        else binding.includeLoading.progressBar.visibility = View.GONE
    }

    private fun validationInput(name: String, email: String, password: String): Boolean {
        return if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            true
        } else {
            Toast.makeText(requireActivity(), R.string.validate_all_input, Toast.LENGTH_SHORT)
                .show()
            false
        }


    }

    private fun isRegister(token: String) {
        if (token != "") {
            val intentToMain = Intent(requireActivity(), MainActivity::class.java)
            intentToMain.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            startActivity(intentToMain)
            Toast.makeText(requireContext(), R.string.welcome_text, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}