package com.pepe.mycars

import com.pepe.mycars.app.data.domain.repository.AuthRepository
import com.pepe.mycars.app.viewmodel.AuthViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthViewModelTest {
    private val authRepository: AuthRepository = mock()
    private val tested = AuthViewModel(authRepository)

    @Test
    fun testRegistration() = runTest {
        val email = "email"
        val password = "pass"
        val autoLogin = false
        tested.login(email, password, autoLogin)

        verify(authRepository, times(1)).login(email, password, autoLogin)

    }
}