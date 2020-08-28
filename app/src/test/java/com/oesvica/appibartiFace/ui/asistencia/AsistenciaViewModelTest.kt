package com.oesvica.appibartiFace.ui.asistencia

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.oesvica.appibartiFace.TestingCoroutineContextProvider
import com.oesvica.appibartiFace.data.model.asistencia.AsistenciaFilter
import com.oesvica.appibartiFace.data.model.toCustomDate
import com.oesvica.appibartiFace.data.repository.ReportsRepository
import com.oesvica.appibartiFace.utils.MainCoroutineRule
import com.oesvica.appibartiFace.utils.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule

class AsistenciaViewModelTest {

    @get:Rule val instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val coroutineRule = MainCoroutineRule()

    private lateinit var viewModel: AsistenciaViewModel
    private lateinit var reportsRepository: ReportsRepository

    @Before
    fun setUp() {
        reportsRepository = mock()
        viewModel = AsistenciaViewModel(reportsRepository, TestingCoroutineContextProvider())
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `should refresh asistencias`() = coroutineRule.runBlockingTest {
        // Given
        val iniDate = "2020-08-16".toCustomDate()!!
        val endDate = "2020-08-18".toCustomDate()!!

        // When
        viewModel.refreshAsistencias(iniDate, endDate)

        // Then
        assertEquals(
            AsistenciaFilter(
                iniDate,
                endDate
            ), viewModel.asistenciasQueryRange.getOrAwaitValue())
        verify(reportsRepository, times(1)).refreshAsistencias(iniDate, endDate)
    }
}