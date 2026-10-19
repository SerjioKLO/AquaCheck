package com.example.aquacheck.model

import android.graphics.Bitmap
import java.util.UUID

enum class RolUsuario {
    SUPERVISOR,
    JEFE
}

data class Usuario(
    val id: String,
    val nombre: String,
    val username: String,
    val rol: RolUsuario
)

data class Buzo(
    val id: String,
    val nombre: String,
    val certificacion: String
)

data class Supervisor(
    val id: String,
    val nombre: String
)

data class Centro(
    val id: String,
    val nombre: String,
    val ubicacion: String
)

enum class EstadoItem {
    CUMPLE,
    NO_CUMPLE,
    OBSERVADO,
    PENDIENTE,
    NO_APLICA
}

data class ItemChecklist(
    val id: String,
    val nombre: String,
    val estado: EstadoItem = EstadoItem.CUMPLE,
    val esCritico: Boolean = false
)

data class EvidenciaItem(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val imagePath: String? = null,
    val bitmap: Bitmap? = null,
    val drawableRes: Int? = null
)

data class PreChequeo(
    val id: String,
    val buzo: Buzo,
    val supervisor: Supervisor,
    val centro: Centro,
    val fecha: String,
    val items: List<ItemChecklist>,
    val evidencias: List<EvidenciaItem> = emptyList(),
    val resultado: String = "Apto",
    val observaciones: String = "",
    val salud: DatosSalud? = null,
    val nivelSalud: NivelSalud? = null
)

data class Resultado(
    val estado: String,
    val fecha: String,
    val resumenChecklist: String
)

enum class NivelSalud{
    NORMAL,
    ATENCION,
    CRITICA
}

data class DatosSalud(
    val presionsistolica: Int? = null,
    val presionDiastolica: Int? = null,
    val saturacionOxigeno: Int? = null,
    val durmioMenos6Hrs: Boolean = false,
    val resfrioDolorOidos: Boolean = false,
    val consumioAlcohol24hrs: Boolean = false
)


