package com.oesvica.appibartiFace.ui.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.oesvica.appibartiFace.TestingCoroutineContextProvider
import com.oesvica.appibartiFace.asResult
import org.junit.Before
import com.oesvica.appibartiFace.data.model.FirebaseTokenId
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.auth.AuthInfo
import com.oesvica.appibartiFace.data.model.auth.LogInResponse
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.data.repository.UserRepository
import com.oesvica.appibartiFace.utils.MainCoroutineRule
import com.oesvica.appibartiFace.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()


    private lateinit var viewModel: MainViewModel
    private lateinit var userRepository: UserRepository
    private lateinit var maestrosRepository: MaestrosRepository

    @Before
    fun setUp() {
        userRepository = mock()
        maestrosRepository = mock()
        viewModel = MainViewModel(userRepository, maestrosRepository, TestingCoroutineContextProvider())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should login successfully`() = coroutineRule.runBlockingTest {
        // Given
        val user = "test"
        val password = "test123456"
        val token = "4fh65f645j65k65h65kl"
        val firebaseToken = "jhfhxdgj45fj45dh45k465dk46d645k65"
        val loginResult = Result(LogInResponse(logIn = true, token = token))
        whenever(userRepository.logIn(user = user, password = password)).thenReturn(loginResult)
        val authInfo = AuthInfo(logIn = true, token = token)
        whenever(userRepository.getAuthInfo()).thenReturn(authInfo)
        whenever(userRepository.getFirebaseTokenId()).thenReturn(firebaseToken)
        whenever(userRepository.sendFirebaseTokenId(FirebaseTokenId(firebaseToken))).thenReturn(
            FirebaseTokenId(firebaseToken).asResult()
        )

        // When
        viewModel.logIn(user, password)

        // Then
        assertEquals(authInfo.asResult(), viewModel.authInfo.getOrAwaitValue())
    }
}