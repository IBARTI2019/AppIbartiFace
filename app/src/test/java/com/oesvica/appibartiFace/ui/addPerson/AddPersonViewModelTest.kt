package com.oesvica.appibartiFace.ui.addPerson

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.oesvica.appibartiFace.*
import com.oesvica.appibartiFace.data.model.NetworkRequestStatus
import com.oesvica.appibartiFace.data.model.Result
import com.oesvica.appibartiFace.data.model.category.Category
import com.oesvica.appibartiFace.data.model.status.Status
import com.oesvica.appibartiFace.data.repository.MaestrosRepository
import com.oesvica.appibartiFace.utils.MainCoroutineRule
import com.oesvica.appibartiFace.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class AddPersonViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AddPersonViewModel
    private lateinit var maestrosRepository: MaestrosRepository

    @Before
    fun setUp() {
        maestrosRepository = mock()
        viewModel = AddPersonViewModel(maestrosRepository, TestingCoroutineContextProvider())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should load predictions for standby`() = coroutineRule.runBlockingTest {
        // Given
        val standBy = getStandBy()
        val predictions = listOf(getPrediction())
        val result = Result(predictions)
        whenever(maestrosRepository.fetchPredictionsByStandBy(standBy)).thenReturn(result)

        // When
        viewModel.loadPredictionsForStandBy(standBy)

        // Then
        assertEquals(result, viewModel.predictions.getOrAwaitValue())
        verify(maestrosRepository, times(1)).fetchPredictionsByStandBy(any())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should not fetch predictions for standby already cached`() =
        coroutineRule.runBlockingTest {
            // Given
            val standBy = getStandBy()
            val predictions = listOf(getPrediction())
            viewModel.predictions.value = predictions.asResult()

            // When
            viewModel.loadPredictionsForStandBy(standBy)

            // Then
            verify(maestrosRepository, times(0)).fetchPredictionsByStandBy(any())
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should fetch categories when cache is empty`() = coroutineRule.runBlockingTest {
        // Given
        val list = listOf(getCategory())
        whenever(maestrosRepository.findCategoriesSynchronous()).doReturn(emptyList(), list)
        whenever(maestrosRepository.refreshCategories()).thenReturn(Unit.asResult())

        // When
        val categories = viewModel.categories.getOrAwaitValue()

        // Then
        assertEquals(list, categories)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should display emptyList of categories when cache is empty and fetching fails`() =
        coroutineRule.runBlockingTest {
            // Given
            whenever(maestrosRepository.findCategoriesSynchronous()).thenReturn(emptyList())
            whenever(maestrosRepository.refreshCategories()).thenReturn(Result(error = Throwable()))

            // When
            val categories = viewModel.categories.getOrAwaitValue()

            // Then
            verify(maestrosRepository, times(1)).findCategoriesSynchronous()
            verify(maestrosRepository, times(1)).refreshCategories()
            assertEquals(emptyList<Category>(), categories)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should not fetch categories when cache is not empty`() = coroutineRule.runBlockingTest {
        // Given
        val list = listOf(getCategory())
        whenever(maestrosRepository.findCategoriesSynchronous()).thenReturn(list)

        // When
        val categories = viewModel.categories.getOrAwaitValue()

        // Then
        verify(maestrosRepository, never()).refreshCategories()
        assertEquals(list, categories)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should fetch statuses when cache is empty`() = coroutineRule.runBlockingTest {
        // Given
        val list = listOf(getStatus())
        whenever(maestrosRepository.findStatusesSynchronous()).doReturn(emptyList(), list)
        whenever(maestrosRepository.refreshStatuses()).thenReturn(list.asResult())

        // When
        val statuses = viewModel.statuses.getOrAwaitValue()

        // Then
        assertEquals(list, statuses)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should display emptyList of statuses when cache is empty and fetching fails`() =
        coroutineRule.runBlockingTest {
            // Given
            whenever(maestrosRepository.findStatusesSynchronous()).thenReturn(emptyList())
            whenever(maestrosRepository.refreshStatuses()).thenReturn(Result(error = Throwable()))

            // When
            val statuses = viewModel.statuses.getOrAwaitValue()

            // Then
            verify(maestrosRepository, times(1)).findStatusesSynchronous()
            verify(maestrosRepository, times(1)).refreshStatuses()
            assertEquals(emptyList<Status>(), statuses)
        }

    @ExperimentalCoroutinesApi
    @Test
    fun `should not fetch statuses when cache is not empty`() = coroutineRule.runBlockingTest {
        // Given
        val list = listOf(getStatus())
        whenever(maestrosRepository.findStatusesSynchronous()).thenReturn(list)

        // When
        val statuses = viewModel.statuses.getOrAwaitValue()

        // Then
        verify(maestrosRepository, never()).refreshStatuses()
        assertEquals(list, statuses)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should add a person successfully`() = coroutineRule.runBlockingTest {
        // Given
        whenever(maestrosRepository.insertPerson(any())).thenReturn(Unit.asResult())

        // When
        val mockedObserver = mock<Observer<NetworkRequestStatus>>()
        viewModel.addPersonNetworkRequest.observeForever(mockedObserver)
        viewModel.addPerson(
            cedula = "1321",
            category = "xd",
            date = "2020-05-12",
            device = "0001",
            client = "001",
            photo = "xd.jpg",
            status = "ok"
        )

        // Then
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = true))
        verify(mockedObserver).onChanged(NetworkRequestStatus(isOngoing = false))
        assertEquals(
            NetworkRequestStatus(isOngoing = false),
            viewModel.addPersonNetworkRequest.getOrAwaitValue()
        )
    }

}