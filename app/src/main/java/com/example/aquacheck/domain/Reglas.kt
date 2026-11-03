package com.example.aquacheck.domain

import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.ItemChecklist
import com.example.aquacheck.model.NivelSalud

/**
 * Objeto Singleton que encapsula las Reglas de Negocio puras de la aplicación AquaCheck
 * (Capa de Dominio limpia - Sin dependencias de Android, Compose o Room).
 */
object Reglas {

    /**
     * Función A: Evaluación Médica Consolidada (nivelSalud)
     *
     * Evalúa y pondera los signos vitales y respuestas de la encuesta aplicando un algoritmo
     * de "peor escenario" (Prioridad: Crítico > Atención > Normal).
     *
     * Límites lógicos absolutos de entrada (Min/Max):
     * - SpO2: Min 50%, Max 100%
     * - Presión Sistólica: Min 70, Max 250
     * - Presión Diastólica: Min 40, Max 150
     *
     * Umbrales académicos:
     * - SpO2: Crítico < 92%, Atención < 95%, Normal >= 95%
     * - Presión Sistólica: Crítico >= 160, Atención >= 140, Normal < 140
     * - Presión Diastólica: Crítico >= 100, Atención >= 90, Normal < 90
     * - Encuesta (Alertas): Atención si alertasCount > 0
     *
     * Si cualquier parámetro está fuera de los límites de entrada mínimos o máximos,
     * el estado se fuerza inmediatamente a NivelSalud.CRITICA.
     */
    fun nivelSalud(
        saturacion: Int,
        sistolica: Int,
        diastolica: Int,
        alertasCount: Int = 0
    ): NivelSalud {
        // 1. Validar límites de entrada mínimos y máximos absolutos
        if (saturacion !in 50..100 || sistolica !in 70..250 || diastolica !in 40..150) {
            return NivelSalud.CRITICA
        }

        // 2. Evaluaciones para Nivel CRITICA
        if (saturacion < 92 || sistolica >= 160 || diastolica >= 100) {
            return NivelSalud.CRITICA
        }

        // 3. Evaluaciones para Nivel ATENCION
        if (saturacion < 95 || sistolica >= 140 || diastolica >= 90 || alertasCount > 0) {
            return NivelSalud.ATENCION
        }

        // 4. Si supera todas las pruebas de riesgo
        return NivelSalud.NORMAL
    }

    /**
     * Sobrecarga conveniente de nivelSalud que recibe parámetros individuales de encuesta.
     */
    fun nivelSalud(
        saturacion: Int,
        sistolica: Int,
        diastolica: Int,
        horasSueno: Int = 8,
        horasUltimoAlcohol: Int = 24,
        tieneCongestionNasal: Boolean = false,
        tomaMedicamentosNuevos: Boolean = false,
        estadoPsicologicoCalmo: Boolean = true
    ): NivelSalud {
        var alertas = 0
        if (horasSueno < 6) alertas++
        if (horasUltimoAlcohol <= 12) alertas++
        if (tieneCongestionNasal) alertas++
        if (tomaMedicamentosNuevos) alertas++
        if (!estadoPsicologicoCalmo) alertas++

        return nivelSalud(
            saturacion = saturacion,
            sistolica = sistolica,
            diastolica = diastolica,
            alertasCount = alertas
        )
    }

    /**
     * Función B: Dictamen Final del Pre-chequeo (resultado)
     *
     * Cruza el NivelSalud con el checklist de equipamiento.
     * Retorna estrictamente uno de los tres estados:
     * - "Requiere revisión": Si la salud es CRITICA o si AL MENOS UN ítem con esCritico == true tiene el estado NO_CUMPLE.
     * - "Observado": Si no es crítico, pero la salud es ATENCION, o si cualquier ítem del checklist tiene estado OBSERVADO o NO_CUMPLE (no crítico).
     * - "Cumple": Si la salud es NORMAL y todos los ítems evaluados tienen estado CUMPLE o NO_APLICA.
     */
    fun resultado(
        nivelSalud: NivelSalud,
        checklistItems: List<ItemChecklist>
    ): String {
        // Verificar si existe alguna falla en un ítem crítico del equipamiento
        val tieneFallaCriticaChecklist = checklistItems.any { item ->
            item.esCritico && item.estado == EstadoItem.NO_CUMPLE
        }

        if (nivelSalud == NivelSalud.CRITICA || tieneFallaCriticaChecklist) {
            return "Requiere revisión"
        }

        // Verificar si existe alguna observación o falla en ítem no crítico
        val tieneObservacionOCuplimientoFallido = checklistItems.any { item ->
            item.estado == EstadoItem.OBSERVADO || item.estado == EstadoItem.NO_CUMPLE
        }

        if (nivelSalud == NivelSalud.ATENCION || tieneObservacionOCuplimientoFallido) {
            return "Observado"
        }

        return "Cumple"
    }
}
