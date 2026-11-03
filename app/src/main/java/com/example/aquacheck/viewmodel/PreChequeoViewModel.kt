package com.example.aquacheck.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.AppDatabase
import com.example.aquacheck.data.PreChequeoDao
import com.example.aquacheck.data.PreChequeoRepository
import com.example.aquacheck.data.SupabaseSyncManager
import com.example.aquacheck.domain.Reglas
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
    private var dao: PreChequeoDao? = null

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

    private var appContext: Context? = null

    fun initDatabase(context: Context) {
        appContext = context.applicationContext
        initSession(context)
        if (repository != null) return
        try {
            val d = AppDatabase.getDatabase(context).preChequeoDao()
            dao = d
            val repo = PreChequeoRepository(d)
            repository = repo
            viewModelScope.launch {
                try {
                    d.deleteAllMockData()
                } catch (e: Exception) {
                    // Ignore
                }
                repo.getAllPreChequeos().collectLatest { list ->
                    _uiState.update { it.copy(preChequeos = list) }
                }
            }
        } catch (e: Exception) {
            // Fallback if database fails
        }
    }

    fun sincronizarConSupabase(onComplete: (Boolean, String) -> Unit) {
        val currentDao = dao
        if (currentDao == null) {
            onComplete(false, "Base de datos no inicializada")
            return
        }
        viewModelScope.launch {
            val syncManager = SupabaseSyncManager(currentDao)
            val result = syncManager.sincronizar()
            if (result.isSuccess) {
                val count = result.getOrDefault(0)
                onComplete(true, "Sincronización completada ($count registros procesados)")
            } else {
                val err = result.exceptionOrNull()?.message ?: "Error al conectar con Supabase"
                onComplete(false, "Error de sincronización: $err")
            }
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

        val fc = state.frecuenciaCardiacaText.toIntOrNull()
        val sis = state.sistolicaText.toIntOrNull()
        val dia = state.diastolicaText.toIntOrNull()
        val sat = state.saturacionText.toIntOrNull()
        val sueno = state.horasSuenoText.toIntOrNull()
        val alcohol = state.horasUltimoAlcoholText.toIntOrNull()

        if (state.frecuenciaCardiacaText.isBlank()) fallas.add("Frecuencia Cardíaca: Campo sin ingresar")
        if (state.sistolicaText.isBlank()) fallas.add("Presión Sistólica: Campo sin ingresar")
        if (state.diastolicaText.isBlank()) fallas.add("Presión Diastólica: Campo sin ingresar")
        if (state.saturacionText.isBlank()) fallas.add("Saturación SpO2: Campo sin ingresar")
        if (state.horasSuenoText.isBlank()) fallas.add("Horas de Sueño: Campo sin ingresar")
        if (state.horasUltimoAlcoholText.isBlank()) fallas.add("Tiempo sin Alcohol: Campo sin ingresar")

        // Recolectar lista de mensajes descriptivos para la UI
        if (fc != null && fc !in 50..100) fallas.add("Frecuencia Cardíaca ($fc lpm): debe estar entre 50 y 100 lpm")
        if (sis != null && sis !in 90..140) fallas.add("Presión Sistólica ($sis mmHg): debe estar entre 90 y 140 mmHg")
        if (dia != null && dia !in 60..90) fallas.add("Presión Diastólica ($dia mmHg): debe estar entre 60 y 90 mmHg")
        if (sat != null && sat !in 95..100) fallas.add("Saturación SpO2 ($sat%): debe ser mínimo 95%")
        if (sueno != null && sueno < 6) fallas.add("Horas de Sueño (${sueno}h): mínimo 6 horas de descanso")
        if (alcohol != null && alcohol <= 12) fallas.add("Consumo de Alcohol (${alcohol}h): requiere mínimo 12 horas libre de alcohol")
        if (state.tieneCongestionNasal) fallas.add("Congestión Nasal/Sinusal: Incompatible con inmersión")
        if (state.tomaMedicamentosNuevos) fallas.add("Medicamentos Nuevos/Sedantes: Incompatible con buceo")
        if (!state.estadoPsicologicoCalmo) fallas.add("Estado Psicológico: Requiere estado calmo")

        // Delegar la decisión algorítmica médica al objeto de dominio Reglas
        val nivelSalud = Reglas.nivelSalud(
            saturacion = sat ?: 0,
            sistolica = sis ?: 0,
            diastolica = dia ?: 0,
            horasSueno = sueno ?: 0,
            horasUltimoAlcohol = alcohol ?: 0,
            tieneCongestionNasal = state.tieneCongestionNasal,
            tomaMedicamentosNuevos = state.tomaMedicamentosNuevos,
            estadoPsicologicoCalmo = state.estadoPsicologicoCalmo
        )

        val esApto = (nivelSalud == NivelSalud.NORMAL) && fallas.isEmpty()
        val estadoRes = when (nivelSalud) {
            NivelSalud.NORMAL -> if (fallas.isEmpty()) "Cumple" else "Observado"
            NivelSalud.ATENCION -> "Observado"
            NivelSalud.CRITICA -> "Requiere revisión"
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

        val sis = currentState.sistolicaText.toIntOrNull() ?: 0
        val dia = currentState.diastolicaText.toIntOrNull() ?: 0
        val sat = currentState.saturacionText.toIntOrNull() ?: 0
        val sueno = currentState.horasSuenoText.toIntOrNull() ?: 0
        val alcohol = currentState.horasUltimoAlcoholText.toIntOrNull() ?: 0

        // 1. Delegar evaluación médica a la capa de dominio Reglas
        val nivelSalud = Reglas.nivelSalud(
            saturacion = sat,
            sistolica = sis,
            diastolica = dia,
            horasSueno = sueno,
            horasUltimoAlcohol = alcohol,
            tieneCongestionNasal = currentState.tieneCongestionNasal,
            tomaMedicamentosNuevos = currentState.tomaMedicamentosNuevos,
            estadoPsicologicoCalmo = currentState.estadoPsicologicoCalmo
        )

        // 2. Delegar dictamen final cruzando salud con checklist a la capa de dominio Reglas
        val estadoFinal = Reglas.resultado(
            nivelSalud = nivelSalud,
            checklistItems = currentState.checklistItems
        )

        val items = currentState.checklistItems
        val cumpleCount = items.count { it.estado == EstadoItem.CUMPLE }
        val noCumpleCount = items.count { it.estado == EstadoItem.NO_CUMPLE }
        val observadoCount = items.count { it.estado == EstadoItem.OBSERVADO }

        val resumen = when (estadoFinal) {
            "Cumple" -> "Salud Apta | ${items.size}/${items.size} Equipamiento Cumple"
            "Requiere revisión" -> "Salud/Equipamiento Crítico (Requiere revisión)"
            else -> {
                val partes = mutableListOf<String>()
                if (cumpleCount > 0) partes.add("$cumpleCount Cumple")
                if (observadoCount > 0) partes.add("$observadoCount Observado")
                if (noCumpleCount > 0) partes.add("$noCumpleCount No Cumple")
                "Nivel Salud: $nivelSalud | " + partes.joinToString(", ")
            }
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

        val nivelSaludCalculado = Reglas.nivelSalud(
            saturacion = satInt,
            sistolica = sisInt,
            diastolica = diaInt,
            horasSueno = suenoInt,
            horasUltimoAlcohol = alcoholInt,
            tieneCongestionNasal = currentState.tieneCongestionNasal,
            tomaMedicamentosNuevos = currentState.tomaMedicamentosNuevos,
            estadoPsicologicoCalmo = currentState.estadoPsicologicoCalmo
        )

        val nuevoPreChequeo = PreChequeo(
            id = UUID.randomUUID().toString(),
            buzo = buzo,
            supervisor = supervisor,
            centro = centro,
            fecha = currentState.currentDate,
            items = currentState.checklistItems,
            evidencias = currentState.evidencias,
            resultado = resultadoCalculado.estado,
            nivelSalud = nivelSaludCalculado,
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
