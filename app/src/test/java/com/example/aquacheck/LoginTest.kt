package com.example.aquacheck

import com.example.aquacheck.model.RolUsuario
import com.example.aquacheck.viewmodel.PreChequeoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class LoginTest {

    private lateinit var viewModel: PreChequeoViewModel

    @Before
    fun setUp() {
        viewModel = PreChequeoViewModel()
    }

    @Test
    fun login_conSupervisorCorrecto_retornaTrueYAsignaRolSupervisor() {
        val result = viewModel.login("supervisor", "1234")
        assertTrue(result)

        val currentUser = viewModel.uiState.value.currentUser
        assertNotNull(currentUser)
        assertEquals("supervisor", currentUser?.username)
        assertEquals(RolUsuario.SUPERVISOR, currentUser?.rol)
    }

    @Test
    fun login_conJefeCorrecto_retornaTrueYAsignaRolJefe() {
        val result = viewModel.login("jefe", "1234")
        assertTrue(result)

        val currentUser = viewModel.uiState.value.currentUser
        assertNotNull(currentUser)
        assertEquals("jefe", currentUser?.username)
        assertEquals(RolUsuario.JEFE, currentUser?.rol)
    }

    @Test
    fun login_conClaveIncorrecta_retornaFalseYUsuarioEsNull() {
        val result = viewModel.login("supervisor", "wrong_pass")
        assertFalse(result)
        assertNull(viewModel.uiState.value.currentUser)
    }

    @Test
    fun login_conUsuarioInvalido_retornaFalseYUsuarioEsNull() {
        val result = viewModel.login("admin", "1234")
        assertFalse(result)
        assertNull(viewModel.uiState.value.currentUser)
    }

    @Test
    fun logout_limpiaElUsuarioActivo() {
        viewModel.login("supervisor", "1234")
        assertNotNull(viewModel.uiState.value.currentUser)

        viewModel.logout()
        assertNull(viewModel.uiState.value.currentUser)
    }
}
