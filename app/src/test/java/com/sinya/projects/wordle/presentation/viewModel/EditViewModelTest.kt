package com.sinya.projects.wordle.presentation.viewModel

import app.cash.turbine.test
import com.sinya.projects.wordle.domain.useCase.UpdateNicknameUseCase
import com.sinya.projects.wordle.presentation.edit.EditEvent
import com.sinya.projects.wordle.presentation.edit.EditUiState
import com.sinya.projects.wordle.presentation.edit.EditViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditViewModelTest {

    private lateinit var updateNicknameUseCase: UpdateNicknameUseCase
    private lateinit var viewModel: EditViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        updateNicknameUseCase = mockk()
        viewModel = EditViewModel(updateNicknameUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is EditForm with empty nickname`() = runTest {
        val state = viewModel.state.value
        assertTrue(state is EditUiState.EditForm)
        val form = state as EditUiState.EditForm
        assertEquals("", form.nickname)
        assertFalse(form.isNicknameError)
    }

    @Test
    fun `NicknameChanged updates nickname and clears error`() = runTest {
        viewModel.onEvent(EditEvent.NicknameChanged("Sinya"))
        val state = viewModel.state.value as EditUiState.EditForm
        assertEquals("Sinya", state.nickname)
        assertFalse(state.isNicknameError)
    }

    @Test
    fun `EditClicked with empty nickname - sets isNicknameError`() = runTest {
        viewModel.onEvent(EditEvent.EditClicked)
        val state = viewModel.state.value as EditUiState.EditForm
        assertTrue(state.isNicknameError)
    }

    @Test
    fun `EditClicked with blank nickname - sets isNicknameError`() = runTest {
        viewModel.onEvent(EditEvent.NicknameChanged("   "))
        viewModel.onEvent(EditEvent.EditClicked)
        val state = viewModel.state.value as EditUiState.EditForm
        assertTrue(state.isNicknameError)
    }

    @Test
    fun `EditClicked success - transitions to Success state`() = runTest {
        coEvery { updateNicknameUseCase(any()) } returns Result.success(Unit)

        viewModel.onEvent(EditEvent.NicknameChanged("Sinya"))
        viewModel.onEvent(EditEvent.EditClicked)

        assertTrue(viewModel.state.value is EditUiState.Success)
    }

    @Test
    fun `EditClicked failure - shows error message`() = runTest {
        coEvery { updateNicknameUseCase(any()) } returns Result.failure(RuntimeException("Network error"))

        viewModel.onEvent(EditEvent.NicknameChanged("Sinya"))
        viewModel.onEvent(EditEvent.EditClicked)

        val state = viewModel.state.value as EditUiState.EditForm
        assertNotNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `ErrorShown clears errorMessage`() = runTest {
        coEvery { updateNicknameUseCase(any()) } returns Result.failure(RuntimeException("err"))

        viewModel.onEvent(EditEvent.NicknameChanged("Sinya"))
        viewModel.onEvent(EditEvent.EditClicked)
        viewModel.onEvent(EditEvent.ErrorShown)

        val state = viewModel.state.value as EditUiState.EditForm
        assertNull(state.errorMessage)
    }
}