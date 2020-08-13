package com.oesvica.appibartiFace.ui.main
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import org.junit.Before
//import com.oesvica.appibartiFace.data.model.FirebaseTokenId
//import com.oesvica.appibartiFace.data.model.Result
//import com.oesvica.appibartiFace.data.model.auth.LogInResponse
//import com.oesvica.appibartiFace.data.repository.MaestrosRepository
//import com.oesvica.appibartiFace.data.repository.UserRepository
//import org.junit.Assert.*
//import org.junit.Rule
//import org.junit.Test
//
//class MainViewModelTest {
//
//    @Rule
//    @JvmField val archComponentsRule = InstantTaskExecutorRule()
//
//    private lateinit var viewModel: MainViewModel
//    private lateinit var userRepository: UserRepository
//    private lateinit var maestrosRepository: MaestrosRepository
//
//    @Before
//    fun setUp() {
//        userRepository = mock()
//        maestrosRepository = mock()
//        viewModel = MainViewModel(userRepository, maestrosRepository)
//    }
//
//    @Test
//    fun shouldLogIn() {
//        // Given
//        val user = "test"
//        val password = "test123456"
//        val token = "4fh65f645j65k65h65kl"
//        val firebaseToken = "jhfhxdgj45fj45dh45k465dk46d645k65"
//        val loginResult = Result(LogInResponse(logIn = true, token = token))
//        whenever(userRepository.logIn(user = user, password = password)).thenReturn(loginResult)
//        whenever(userRepository.getFirebaseTokenId()).thenReturn(firebaseToken)
//        whenever(userRepository.sendFirebaseTokenId(FirebaseTokenId(firebaseToken))).thenReturn(FirebaseTokenId(firebaseToken))
//
//        assertEquals(loginResult, viewModel.authInfo.value)
//
//    }
//}