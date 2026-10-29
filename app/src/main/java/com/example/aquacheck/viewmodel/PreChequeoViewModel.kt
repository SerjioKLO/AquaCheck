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
import com.example.aquacheck.model.NivelSalud
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
            preChequeos = MockData.mockPreChequeos,
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

    private var appContext: Context? = null

    fun initDatabase(context: Context) {
        appContext = context.applicationContext
        initSession(context)
        if (repository != null) return
        try {
            val dao = AppDatabase.getDatabase(context).preChequeoDao()
            val repo = PreChequeoRepository(dao)
            repository = repo
            viewModelScope.launch {
                repo.getAllPreChequeos().collectLatest { list ->
                    val preChequeosList = if (list.isEmpty()) {
                        MockData.mockPreChequeos.forEach { mock ->
                            repo.insertPreChequeo(mock)
                        }
                        MockData.mockPreChequeos
                    } else {
                        list
                    }
                    _uiState.update { it.copy(preChequeos = preChequeosList) }
                }
            }
        } catch (e: Exception) {
            // Fallback if database fails
        }
    }

    private fun initSession(context: Context) {
        try {
            val prefs = context.getSharedPreferences("aquacheck_session", Context.MODE_PRIVATE)
            val savedUsername = prefs.getString("logged_in_username", null)
            if (savedUsername != null) {
                val userObj = MockData.mockUsuarios.find { it.username == savedUsername }
                if (userObj != null) {
                    val autoSupervisor = Supervisor(id = userObj.id, nombre = userObj.nombre)
                    _uiState.update {
                        it.copy(
                            currentUser = userObj,
                            currentSupervisor = autoSupervisor,
                            profileNombre = userObj.nombre
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Fallback
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
            val autoSupervisor = Supervisor(id = userObj.id, nombre = userObj.nombre)
            try {
                appContext?.let { ctx ->
                    val prefs = ctx.getSharedPreferences("aquacheck_session", Context.MODE_PRIVATE)
                    prefs.edit().putString("logged_in_username", userObj.username).apply()
                }
            } catch (e: Exception) {
                // Fallback
            }
            _uiState.update {
                it.copy(
                    currentUser = userObj,
                    currentSupervisor = autoSupervisor,
                    currentDate = getCurrentFormattedDate(),
                    profileNombre = userObj.nombre
                )
            }
            true
        } else {
            false
        }
    }

    fun logout() {
        try {
            appContext?.let { ctx ->
                val prefs = ctx.getSharedPreferences("aquacheck_session", Context.MODE_PRIVATE)
                prefs.edit().remove("logged_in_username").apply()
            }
        } catch (e: Exception) {
            // Fallback
        }
        _uiState.update { it.copy(currentUser = null) }
    }

    private fun getCurrentFormattedDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
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
                currentSupervisor = supervisor ?: currentState.currentSupervisor
            )
        }
    }

    fun setObservacionesText(text: String) {
        _uiState.update { it.copy(observacionesText = text) }
    }

    fun setFrecuenciaCardiaca(text: String) {
        _uiState.update { it.copy(frecuenciaCardiacaText = text, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setSistolica(text: String) {
        _uiState.update { it.copy(sistolicaText = text, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setDiastolica(text: String) {
        _uiState.update { it.copy(diastolicaText = text, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setSaturacion(text: String) {
        _uiState.update { it.copy(saturacionText = text, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setDurmioMenos6hrs(value: Boolean) {
        _uiState.update { it.copy(durmioMenos6hrs = value, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setResfrioDolorOidos(value: Boolean) {
        _uiState.update { it.copy(resfrioDolorOidos = value, resultado = null) }
        calcularNivelSaludLive()
        validarSalud()
    }

    fun setConsumioAlcohol24hrs(value: Boolean) {
        _uiState.update { it.copy(consumioAlcohol24hrs = value, resultado = null) }
        calcularNivelSaludLive()
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

    fun calcularNivelSaludLive() {
        val state = _uiState.value
        val sis = state.sistolicaText.toIntOrNull()
        val dia = state.diastolicaText.toIntOrNull()
        val sat = state.saturacionText.toIntOrNull()

        val losTresNumerosListos = (sis != null && dia != null && sat != null)

        val nivel = if (losTresNumerosListos) {
            when {
                sat!! < 92 || sis!! >= 160 || dia!! >= 100 -> NivelSalud.CRITICA
                sat < 95 || sis >= 140 || dia >= 90 || state.durmioMenos6hrs || state.resfrioDolorOidos || state.consumioAlcohol24hrs -> NivelSalud.ATENCION
                else -> NivelSalud.NORMAL
            }
        } else null

        _uiState.update { it.copy(nivelSalud = nivel) }
    }

    fun validarSalud(): ResultadoValidacionSalud {
        val state = _uiState.value
        val fallas = mutableListOf<String>()

        val sis = state.sistolicaText.toIntOrNull()
        val dia = state.diastolicaText.toIntOrNull()
        val sat = state.saturacionText.toIntOrNull()

        if (sis == null) {
            fallas.add("Presión Sistólica no ingresada")
        } else if (sis >= 160) {
            fallas.add("Presión Sistólica Crítica ($sis mmHg >= 160)")
        } else if (sis >= 140) {
            fallas.add("Presión Sistólica en Atención ($sis mmHg >= 140)")
        }

        if (dia == null) {
            fallas.add("Presión Diastólica no ingresada")
        } else if (dia >= 100) {
            fallas.add("Presión Diastólica Crítica ($dia mmHg >= 100)")
        } else if (dia >= 90) {
            fallas.add("Presión Diastólica en Atención ($dia mmHg >= 90)")
        }

        if (sat == null) {
            fallas.add("Saturación O₂ no ingresada")
        } else if (sat < 92) {
            fallas.add("Saturación O₂ Crítica ($sat% < 92%)")
        } else if (sat < 95) {
            fallas.add("Saturación O₂ en Atención ($sat% < 95%)")
        }

        if (state.durmioMenos6hrs) {
            fallas.add("Sueño: Durmió menos de 6 horas")
        }
        if (state.resfrioDolorOidos) {
            fallas.add("Síntomas: Resfrío o dolor de oídos")
        }
        if (state.consumioAlcohol24hrs) {
            fallas.add("Alcohol: Consumió alcohol en las últimas 24 horas")
        }

        val esSaludCritica = (sat != null && sat < 92) || (sis != null && sis >= 160) || (dia != null && dia >= 100)
        val esApto = !esSaludCritica && fallas.isEmpty()
        val estadoRes = when {
            esSaludCritica -> "Requiere revisión"
            fallas.isNotEmpty() -> "Atención"
            else -> "Apto"
        }

        val res = ResultadoValidacionSalud(
            esApto = esApto,
            estado = estadoRes,
            observaciones = fallas
        )

        _uiState.update { currentState ->
            currentState.copy(
                saludApto = esApto,
                alertaSalud = if (fallas.isEmpty()) null else fallas.joinToString("\n• "),
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
        val tieneCriticoNoCumple = items.any { it.esCritico && it.estado == EstadoItem.NO_CUMPLE }
        val tieneAlgunaFallaChecklist = items.any { it.estado == EstadoItem.NO_CUMPLE || it.estado == EstadoItem.OBSERVADO }

        val sis = currentState.sistolicaText.toIntOrNull()
        val dia = currentState.diastolicaText.toIntOrNull()
        val sat = currentState.saturacionText.toIntOrNull()

        val esSaludCritica = (sat != null && sat < 92) || (sis != null && sis >= 160) || (dia != null && dia >= 100)
        val esSaludAtencion = (sat != null && sat < 95) || (sis != null && sis >= 140) || (dia != null && dia >= 90) || currentState.durmioMenos6hrs || currentState.resfrioDolorOidos || currentState.consumioAlcohol24hrs

        val estadoFinal = when {
            tieneCriticoNoCumple || esSaludCritica -> "Requiere revisión"
            tieneAlgunaFallaChecklist || esSaludAtencion -> "Observado"
            else -> "Apto"
        }

        val cumpleCount = items.count { it.estado == EstadoItem.CUMPLE || it.estado == EstadoItem.NO_APLICA }
        val noCumpleCount = items.count { it.estado == EstadoItem.NO_CUMPLE }
        val observadoCount = items.count { it.estado == EstadoItem.OBSERVADO }

        val resumen = if (estadoFinal == "Apto") {
            "Salud Normal | Checklist ${cumpleCount}/${items.size} Cumple"
        } else if (estadoFinal == "Requiere revisión") {
            "Requiere Revisión (${validacionSalud.observaciones.size} observaciones)"
        } else {
            val partes = mutableListOf<String>()
            if (cumpleCount > 0) partes.add("$cumpleCount Cumple")
            if (observadoCount > 0) partes.add("$observadoCount Observado")
            if (noCumpleCount > 0) partes.add("$noCumpleCount No Cumple")
            "Observado | " + partes.joinToString(", ")
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
