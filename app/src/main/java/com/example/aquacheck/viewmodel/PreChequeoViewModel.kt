package com.example.aquacheck.viewmodel

import androidx.lifecycle.ViewModel
import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.Resultado
import com.example.aquacheck.model.Supervisor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class PreChequeoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        PreChequeoUiState(
            preChequeos = MockData.mockPreChequeos,
            currentBuzo = MockData.mockBuzos.firstOrNull(),
            currentSupervisor = MockData.mockSupervisores.firstOrNull(),
            currentCentro = MockData.mockCentros.firstOrNull(),
            currentDate = getCurrentFormattedDate(),
            checklistItems = MockData.defaultChecklistItems,
            evidencias = emptyList(),
            resultado = null
        )
    )
    val uiState: StateFlow<PreChequeoUiState> = _uiState.asStateFlow()

    /**
     * Valida las credenciales ingresadas (supervisor / 1234 o jefe / 1234).
     * Retorna true si son válidas y actualiza el usuario activo en el estado.
     */
    fun login(usuario: String, clave: String): Boolean {
        val cleanUsername = usuario.trim().lowercase()
        val userObj = when {
            cleanUsername == "supervisor" && clave == "1234" -> {
                MockData.mockUsuarios.find { it.username == "supervisor" }
            }
            cleanUsername == "jefe" && clave == "1234" -> {
                MockData.mockUsuarios.find { it.username == "jefe" }
            }
            else -> null
        }

        return if (userObj != null) {
            _uiState.update { it.copy(currentUser = userObj) }
            true
        } else {
            false
        }
    }

    /**
     * Cierra la sesión activa limpiando el usuario logueado.
     */
    fun logout() {
        _uiState.update { it.copy(currentUser = null) }
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    /**
     * Agrega una nueva evidencia fotográfica al estado actual.
     */
    fun addEvidencia(evidencia: EvidenciaItem) {
        _uiState.update { currentState ->
            currentState.copy(evidencias = currentState.evidencias + evidencia)
        }
    }

    /**
     * Elimina una evidencia por su ID.
     */
    fun removeEvidencia(evidenciaId: String) {
        _uiState.update { currentState ->
            currentState.copy(evidencias = currentState.evidencias.filterNot { it.id == evidenciaId })
        }
    }

    /**
     * Actualiza el estado de un item del checklist por su id.
     */
    fun updateItemStatus(itemId: String, nuevoEstado: EstadoItem) {
        _uiState.update { currentState ->
            val updatedItems = currentState.checklistItems.map { item ->
                if (item.id == itemId) item.copy(estado = nuevoEstado) else item
            }
            currentState.copy(
                checklistItems = updatedItems,
                resultado = null
            )
        }
    }

    fun updateItemEstado(itemId: String, nuevoEstado: EstadoItem) = updateItemStatus(itemId, nuevoEstado)

    /**
     * Setea los datos generales del formulario de pre-chequeo.
     */
    fun setFormData(
        centro: Centro? = _uiState.value.currentCentro,
        fecha: String = _uiState.value.currentDate,
        buzo: Buzo? = _uiState.value.currentBuzo,
        supervisor: Supervisor? = _uiState.value.currentSupervisor
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                currentCentro = centro,
                currentDate = fecha,
                currentBuzo = buzo,
                currentSupervisor = supervisor
            )
        }
    }

    fun setCentro(centro: Centro?) {
        _uiState.update { it.copy(currentCentro = centro) }
    }

    fun setFecha(fecha: String) {
        _uiState.update { it.copy(currentDate = fecha) }
    }

    fun setBuzo(buzo: Buzo?) {
        _uiState.update { it.copy(currentBuzo = buzo) }
    }

    fun setSupervisor(supervisor: Supervisor?) {
        _uiState.update { it.copy(currentSupervisor = supervisor) }
    }

    /**
     * Calcula el resultado final según las reglas de negocio:
     * - Si CUALQUIER item es NO_CUMPLE u OBSERVADO, el resultado es "Observado".
     * - Si TODOS son CUMPLE, el resultado es "Apto".
     */
    fun calculateResult(): Resultado {
        val currentState = _uiState.value
        val items = currentState.checklistItems

        val isApto = items.all { it.estado == EstadoItem.CUMPLE }
        val estadoResult = if (isApto) "Apto" else "Observado"

        val cumpleCount = items.count { it.estado == EstadoItem.CUMPLE }
        val noCumpleCount = items.count { it.estado == EstadoItem.NO_CUMPLE }
        val observadoCount = items.count { it.estado == EstadoItem.OBSERVADO }

        val resumen = if (isApto) {
            "${items.size}/${items.size} Cumple"
        } else {
            val partes = mutableListOf<String>()
            if (cumpleCount > 0) partes.add("$cumpleCount Cumple")
            if (observadoCount > 0) partes.add("$observadoCount Observado")
            if (noCumpleCount > 0) partes.add("$noCumpleCount No Cumple")
            partes.joinToString(", ")
        }

        val resultadoCalculado = Resultado(
            estado = estadoResult,
            fecha = currentState.currentDate,
            resumenChecklist = resumen
        )

        _uiState.update { it.copy(resultado = resultadoCalculado) }
        return resultadoCalculado
    }

    fun calcularResultado(): Resultado = calculateResult()

    /**
     * Guarda / confirma el pre-chequeo actual agregándolo al historial de preChequeos.
     */
    fun commitPreCheck(): PreChequeo? {
        val currentState = _uiState.value
        val buzo = currentState.currentBuzo ?: return null
        val supervisor = currentState.currentSupervisor ?: return null
        val centro = currentState.currentCentro ?: return null

        val resultadoCalculado = currentState.resultado ?: calculateResult()

        val nuevoPreChequeo = PreChequeo(
            id = UUID.randomUUID().toString(),
            buzo = buzo,
            supervisor = supervisor,
            centro = centro,
            fecha = currentState.currentDate,
            items = currentState.checklistItems,
            evidencias = currentState.evidencias,
            resultado = resultadoCalculado.estado
        )

        _uiState.update { state ->
            state.copy(
                preChequeos = listOf(nuevoPreChequeo) + state.preChequeos,
                checklistItems = MockData.defaultChecklistItems,
                evidencias = emptyList(),
                resultado = null
            )
        }

        return nuevoPreChequeo
    }

    fun commitPreChequeo(): PreChequeo? = commitPreCheck()

    fun guardarPreChequeo(): PreChequeo? = commitPreCheck()
}
