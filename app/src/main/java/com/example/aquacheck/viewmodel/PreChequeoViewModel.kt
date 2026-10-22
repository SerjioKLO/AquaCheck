package com.example.aquacheck.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.AppDatabase
import com.example.aquacheck.data.PreChequeoRepository
import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.DatosSalud
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.Resultado
import com.example.aquacheck.model.Supervisor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class ResultadoValidacionSalud(
    val esApto: Boolean,
    val estado: String,
    val observaciones: List<String>
)

open class PreChequeoViewModel : ViewModel() {

    private var repository: PreChequeoRepository? = null

    private val _uiState = MutableStateFlow(
        PreChequeoUiState(
            preChequeos = emptyList(),
            currentBuzo = MockData.mockBuzos.firstOrNull(),
            currentSupervisor = MockData.mockSupervisores.firstOrNull(),
            currentCentro = MockData.mockCentros.firstOrNull(),
            currentDate = getCurrentFormattedDate(),
            checklistItems = MockData.defaultChecklistItems,
            evidencias = emptyList(),
            resultado = null,
            searchQuery = "",
            selectedFilter = "Todos"
        )
    )
    val uiState: StateFlow<PreChequeoUiState> = _uiState.asStateFlow()

    fun initDatabase(context: Context) {
        if (repository != null) return
        try {
            val dao = AppDatabase.getDatabase(context).preChequeoDao()
            val repo = PreChequeoRepository(dao)
            repository = repo
            viewModelScope.launch {
                repo.deletePreChequeo("pc1")
                repo.deletePreChequeo("pc2")
                repo.getAllPreChequeos().collectLatest { list ->
                    _uiState.update { it.copy(preChequeos = list) }
                }
            }
        } catch (e: Exception) {
            // Fallback if database fails
        }
    }

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
            _uiState.update {
                it.copy(
                    currentUser = userObj,
                    profileNombre = userObj.nombre
                )
            }
            true
        } else {
            false
        }
    }

    fun logout() {
        _uiState.update { it.copy(currentUser = null) }
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun addEvidencia(evidencia: EvidenciaItem) {
        _uiState.update { currentState ->
            currentState.copy(evidencias = currentState.evidencias + evidencia)
        }
    }

    fun removeEvidencia(evidenciaId: String) {
        _uiState.update { currentState ->
            currentState.copy(evidencias = currentState.evidencias.filterNot { it.id == evidenciaId })
        }
    }

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

    fun setFrecuenciaCardiaca(text: String) {
        _uiState.update { it.copy(frecuenciaCardiacaText = text, resultado = null) }
        validarSalud()
    }

    fun setSistolica(text: String) {
        _uiState.update { it.copy(sistolicaText = text, resultado = null) }
        validarSalud()
    }

    fun setDiastolica(text: String) {
        _uiState.update { it.copy(diastolicaText = text, resultado = null) }
        validarSalud()
    }

    fun setSaturacion(text: String) {
        _uiState.update { it.copy(saturacionText = text, resultado = null) }
        validarSalud()
    }

    fun setHorasSueno(text: String) {
        _uiState.update { it.copy(horasSuenoText = text, resultado = null) }
        validarSalud()
    }

    fun setHorasUltimoAlcohol(text: String) {
        _uiState.update { it.copy(horasUltimoAlcoholText = text, resultado = null) }
        validarSalud()
    }

    fun setTieneCongestionNasal(value: Boolean) {
        _uiState.update { it.copy(tieneCongestionNasal = value, resultado = null) }
        validarSalud()
    }

    fun setTomaMedicamentosNuevos(value: Boolean) {
        _uiState.update { it.copy(tomaMedicamentosNuevos = value, resultado = null) }
        validarSalud()
    }

    fun setEstadoPsicologicoCalmo(value: Boolean) {
        _uiState.update { it.copy(estadoPsicologicoCalmo = value, resultado = null) }
        validarSalud()
    }

    fun validarSalud(): ResultadoValidacionSalud {
        val state = _uiState.value
        val fallas = mutableListOf<String>()

        if (state.frecuenciaCardiacaText.isBlank()) {
            fallas.add("Frecuencia Cardíaca: Campo sin ingresar")
        } else {
            val fc = state.frecuenciaCardiacaText.toIntOrNull()
            if (fc == null || fc !in 50..100) {
                fallas.add("Frecuencia Cardíaca (${fc ?: state.frecuenciaCardiacaText} lpm): debe estar entre 50 y 100 lpm")
            }
        }

        if (state.sistolicaText.isBlank()) {
            fallas.add("Presión Sistólica: Campo sin ingresar")
        } else {
            val sis = state.sistolicaText.toIntOrNull()
            if (sis == null || sis !in 90..140) {
                fallas.add("Presión Sistólica (${sis ?: state.sistolicaText} mmHg): debe estar entre 90 y 140 mmHg")
            }
        }

        if (state.diastolicaText.isBlank()) {
            fallas.add("Presión Diastólica: Campo sin ingresar")
        } else {
            val dia = state.diastolicaText.toIntOrNull()
            if (dia == null || dia !in 60..90) {
                fallas.add("Presión Diastólica (${dia ?: state.diastolicaText} mmHg): debe estar entre 60 y 90 mmHg")
            }
        }

        if (state.saturacionText.isBlank()) {
            fallas.add("Saturación SpO2: Campo sin ingresar")
        } else {
            val sat = state.saturacionText.toIntOrNull()
            if (sat == null || sat !in 95..100) {
                fallas.add("Saturación SpO2 (${sat ?: state.saturacionText}%): debe ser mínimo 95%")
            }
        }

        if (state.horasSuenoText.isBlank()) {
            fallas.add("Horas de Sueño: Campo sin ingresar")
        } else {
            val sueno = state.horasSuenoText.toIntOrNull()
            if (sueno == null || sueno < 6) {
                fallas.add("Horas de Sueño (${sueno ?: state.horasSuenoText}h): mínimo 6 horas de descanso")
            }
        }

        if (state.horasUltimoAlcoholText.isBlank()) {
            fallas.add("Tiempo sin Alcohol: Campo sin ingresar")
        } else {
            val alcohol = state.horasUltimoAlcoholText.toIntOrNull()
            if (alcohol == null || alcohol <= 12) {
                fallas.add("Consumo de Alcohol (${alcohol ?: state.horasUltimoAlcoholText}h): requiere mínimo 12 horas libre de alcohol")
            }
        }

        if (state.tieneCongestionNasal) {
            fallas.add("Congestión Nasal/Sinusal: Incompatible con inmersión (riesgo de barotrauma)")
        }

        if (state.tomaMedicamentosNuevos) {
            fallas.add("Medicamentos Nuevos/Sedantes: Incompatible con buceo")
        }

        if (!state.estadoPsicologicoCalmo) {
            fallas.add("Estado Psicológico: Requiere estado calmo y enfocado")
        }

        val esApto = fallas.isEmpty()
        val estadoRes = if (esApto) "Apto" else "Inhabilitado / Requiere Revisión"

        val res = ResultadoValidacionSalud(
            esApto = esApto,
            estado = estadoRes,
            observaciones = fallas
        )

        _uiState.update { currentState ->
            currentState.copy(
                saludApto = esApto,
                alertaSalud = if (esApto) null else fallas.joinToString("\n• "),
                mensajesValidacionSalud = fallas
            )
        }

        return res
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setSelectedFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun updateProfile(nombre: String, rut: String, telefono: String) {
        _uiState.update {
            it.copy(
                profileNombre = nombre,
                profileRut = rut,
                profileTelefono = telefono
            )
        }
    }

    fun updateProfileImageUri(uri: String?) {
        _uiState.update {
            it.copy(profileImageUri = uri)
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

    fun calculateResult(): Resultado {
        val currentState = _uiState.value

        val validacionSalud = validarSalud()
        val items = currentState.checklistItems
        val isChecklistApto = items.all { it.estado == EstadoItem.CUMPLE }

        val estadoFinal = when {
            !validacionSalud.esApto -> "Inhabilitado / Requiere Revisión"
            !isChecklistApto -> "Observado"
            else -> "Apto"
        }

        val cumpleCount = items.count { it.estado == EstadoItem.CUMPLE }
        val noCumpleCount = items.count { it.estado == EstadoItem.NO_CUMPLE }
        val observadoCount = items.count { it.estado == EstadoItem.OBSERVADO }

        val resumen = if (estadoFinal == "Apto") {
            "Salud Apta | ${items.size}/${items.size} Equipamiento Cumple"
        } else if (!validacionSalud.esApto) {
            "Salud Inhabilitada (${validacionSalud.observaciones.size} observaciones)"
        } else {
            val partes = mutableListOf<String>()
            if (cumpleCount > 0) partes.add("$cumpleCount Cumple")
            if (observadoCount > 0) partes.add("$observadoCount Observado")
            if (noCumpleCount > 0) partes.add("$noCumpleCount No Cumple")
            "Salud Apta | " + partes.joinToString(", ")
        }

        val resultadoCalculado = Resultado(
            estado = estadoFinal,
            fecha = currentState.currentDate,
            resumenChecklist = resumen
        )

        _uiState.update { it.copy(resultado = resultadoCalculado) }
        return resultadoCalculado
    }

    fun calcularResultado(): Resultado = calculateResult()

    fun commitPreCheck(): PreChequeo? {
        val currentState = _uiState.value
        val buzo = currentState.currentBuzo ?: return null
        val supervisor = currentState.currentSupervisor ?: return null
        val centro = currentState.currentCentro ?: return null

        val resultadoCalculado = currentState.resultado ?: calculateResult()

        val fcInt = currentState.frecuenciaCardiacaText.toIntOrNull() ?: 0
        val sisInt = currentState.sistolicaText.toIntOrNull() ?: 0
        val diaInt = currentState.diastolicaText.toIntOrNull() ?: 0
        val satInt = currentState.saturacionText.toIntOrNull() ?: 0
        val suenoInt = currentState.horasSuenoText.toIntOrNull() ?: 0
        val alcoholInt = currentState.horasUltimoAlcoholText.toIntOrNull() ?: 0

        val nuevoPreChequeo = PreChequeo(
            id = UUID.randomUUID().toString(),
            buzo = buzo,
            supervisor = supervisor,
            centro = centro,
            fecha = currentState.currentDate,
            items = currentState.checklistItems,
            evidencias = currentState.evidencias,
            resultado = resultadoCalculado.estado,
            frecuenciaCardiaca = fcInt,
            sistolica = sisInt,
            diastolica = diaInt,
            saturacion = satInt,
            horasSueno = suenoInt,
            horasUltimoAlcohol = alcoholInt,
            tieneCongestionNasal = currentState.tieneCongestionNasal,
            tomaMedicamentosNuevos = currentState.tomaMedicamentosNuevos,
            estadoPsicologicoCalmo = currentState.estadoPsicologicoCalmo,
            salud = DatosSalud(
                frecuenciaCardiaca = fcInt,
                sistolica = sisInt,
                diastolica = diaInt,
                saturacion = satInt,
                horasSueno = suenoInt,
                horasUltimoAlcohol = alcoholInt,
                tieneCongestionNasal = currentState.tieneCongestionNasal,
                tomaMedicamentosNuevos = currentState.tomaMedicamentosNuevos,
                estadoPsicologicoCalmo = currentState.estadoPsicologicoCalmo,
                presionsistolica = sisInt,
                presionDiastolica = diaInt,
                saturacionOxigeno = satInt
            )
        )

        val repo = repository
        if (repo != null) {
            viewModelScope.launch {
                repo.insertPreChequeo(nuevoPreChequeo)
            }
        } else {
            _uiState.update { state ->
                state.copy(preChequeos = listOf(nuevoPreChequeo) + state.preChequeos)
            }
        }

        _uiState.update { state ->
            state.copy(
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
