package com.example.storyapp.models

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.storyapp.data.remote.responses.LoginResult
import com.example.storyapp.data.remote.responses.RegisterResponse
import com.example.storyapp.utils.DataDummy
import org.junit.Assert.*
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {

    @get: Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var authViewModel: AuthViewModel
    private val login = DataDummy.dummyLoginEntity()
    private val register = DataDummy.dummyRegisterEntity()

    @Test
    fun login() {
        val observer = Observer<LoginResult> {}

        try {
            val expectedUser = MutableLiveData<LoginResult>()
            expectedUser.value = login
            Mockito.`when`(authViewModel.login).thenReturn(expectedUser)

            val actualUser = MutableLiveData<LoginResult>()
            authViewModel.login .observeForever(observer)
            authViewModel.login(EMAIL, PASSWORD)

            Mockito.verify(authViewModel).login(EMAIL, PASSWORD)
            assertNotNull(actualUser)
        } finally {
            authViewModel.login.removeObserver(observer)
        }
    }

    @Test
    fun register() {
        val observer = Observer<RegisterResponse> {}

        try {
            val expectedUser = MutableLiveData<RegisterResponse>()
            expectedUser.value = register
            Mockito.`when`(authViewModel.register).thenReturn(expectedUser)

            val actualUser = MutableLiveData<RegisterResponse>()
            authViewModel.register .observeForever(observer)
            authViewModel.register(NAME, EMAIL, PASSWORD)

            Mockito.verify(authViewModel).register(NAME, EMAIL, PASSWORD)
            assertNotNull(actualUser)
        } finally {
            authViewModel.register.removeObserver(observer)
        }
    }

    companion object {
        private const val NAME = "exampleName"
        private const val EMAIL = "exampleEmail"
        private const val PASSWORD = "examplePassword"
    }
}