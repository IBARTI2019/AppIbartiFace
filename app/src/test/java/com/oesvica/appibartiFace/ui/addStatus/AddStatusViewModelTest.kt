package com.oesvica.appibartiFace.ui.addStatus

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.oesvica.appibartiFace.TestingCoroutineContextProvider
import com.oesvica.appibartiFace.asResult
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.model.status.StatusRequest
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.MainCoroutineRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddStatusViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddStatusViewModel
    private lateinit var maestrosRepository: MaestrosRepository

    @Before
    fun setUp() {
        maestrosRepository = mock()
        viewModel = AddStatusViewModel(maestrosRepository, TestingCoroutineContextProvider())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should add a status successfully`() = coroutineRule.runBlockingTest {
        // Given
        val statusId = "45645"
        val categoryId = "gdhsj"
        val description = "walk the walk"
        val statusRequest = StatusRequest(categoryId,description)
        val status = Status(
            id = statusId,
            category = categoryId,
            description = description
        )
        given(maestrosRepository.insertStatus(statusRequest)).willReturn(status.asResult())

        // When
        val mockedObserver = mock<Observer<NetworkRequestStatus>>()
        viewModel.addStatusNetworkRequest.observeForever(mockedObserver)
        viewModel.addStatus(null, categoryId, description)

        // Then
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = true))
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = false))
        verify(maestrosRepository, times(1)).insertStatus(statusRequest)
        verify(maestrosRepository, never()).updateStatus(any())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should update a status successfully`() = coroutineRule.runBlockingTest {
        // Given
        val statusId = "45645"
        val categoryId = "gdhsj"
        val description = "walk the walk"
        val status = Status(
            id = statusId,
            category = categoryId,
            description = description
        )
        whenever(maestrosRepository.updateStatus(status)).thenReturn(status.asResult())

        // When
        val mockedObserver = mock<Observer<NetworkRequestStatus>>()
        viewModel.addStatusNetworkRequest.observeForever(mockedObserver)
        viewModel.addStatus(statusId, categoryId, description)

        // Then
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = true))
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = false))
        verify(maestrosRepository, never()).insertStatus(any())
        verify(maestrosRepository, times(1)).updateStatus(status)
    }

}