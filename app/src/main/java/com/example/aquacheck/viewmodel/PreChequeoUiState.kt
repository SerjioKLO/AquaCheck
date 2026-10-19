package com.example.aquacheck.viewmodel

import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.ItemChecklist
import com.example.aquacheck.model.NivelSalud
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.Resultado
import com.example.aquacheck.model.Supervisor
import com.example.aquacheck.model.Usuario

data class PreChequeoUiState(
    val currentUser: Usuario? = null,
    val preChequeos: List<PreChequeo> = emptyList(),
    val currentBuzo: Buzo? = null,
    val currentSupervisor: Supervisor? = null,
    val currentCentro: Centro? = null,
    val currentDate: String = "",
    val checklistItems: List<ItemChecklist> = emptyList(),
    val evidencias: List<EvidenciaItem> = emptyList(),
    val resultado: Resultado? = null,
    val centroText: String = "",
    val buzoText: String = "",
    val sistolicaText: String = "",
    val diastolicaText: String = "",
    val saturacionText: String = "",
    val durmioMenos6hrs: Boolean = false,
    val resfrioDolorOidos: Boolean = false,
    val consumioAlcohol24hrs: Boolean = false,
    val nivelSalud: NivelSalud? = null,
    val observacionesText: String = "",
    val evidenciasUris: List<String> = emptyList()
)
