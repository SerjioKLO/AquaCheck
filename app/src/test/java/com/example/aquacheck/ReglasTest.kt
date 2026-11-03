package com.example.aquacheck

import com.example.aquacheck.domain.Reglas
import com.example.aquacheck.model.EstadoItem
import com.example.aquacheck.model.ItemChecklist
import com.example.aquacheck.model.NivelSalud
import org.junit.Assert.assertEquals
import org.junit.Test

class ReglasTest {

    @Test
    fun nivelSalud_retorna_NORMAL_cuando_todos_los_parametros_estan_dentro_del_rango_optimo() {
        val nivel = Reglas.nivelSalud(
            saturacion = 98,
            sistolica = 120,
            diastolica = 80,
            alertasCount = 0
        )
        assertEquals(NivelSalud.NORMAL, nivel)
    }

    @Test
    fun nivelSalud_retorna_ATENCION_cuando_saturacion_esta_entre_92_y_94() {
        val nivel = Reglas.nivelSalud(
            saturacion = 93,
            sistolica = 120,
            diastolica = 80,
            alertasCount = 0
        )
        assertEquals(NivelSalud.ATENCION, nivel)
    }

    @Test
    fun nivelSalud_retorna_ATENCION_cuando_presion_sistolica_esta_entre_140_y_159() {
        val nivel = Reglas.nivelSalud(
            saturacion = 98,
            sistolica = 145,
            diastolica = 80,
            alertasCount = 0
        )
        assertEquals(NivelSalud.ATENCION, nivel)
    }

    @Test
    fun nivelSalud_retorna_ATENCION_cuando_existen_respuestas_positivas_en_la_encuesta() {
        val nivel = Reglas.nivelSalud(
            saturacion = 98,
            sistolica = 120,
            diastolica = 80,
            alertasCount = 1
        )
        assertEquals(NivelSalud.ATENCION, nivel)
    }

    @Test
    fun nivelSalud_retorna_CRITICA_cuando_la_saturacion_es_menor_a_92() {
        val nivel = Reglas.nivelSalud(
            saturacion = 90,
            sistolica = 120,
            diastolica = 80
        )
        assertEquals(NivelSalud.CRITICA, nivel)
    }

    @Test
    fun nivelSalud_retorna_CRITICA_cuando_la_presion_sistolica_es_mayor_o_igual_a_160() {
        val nivel = Reglas.nivelSalud(
            saturacion = 98,
            sistolica = 165,
            diastolica = 80
        )
        assertEquals(NivelSalud.CRITICA, nivel)
    }

    @Test
    fun nivelSalud_retorna_CRITICA_cuando_los_valores_sobrepasan_los_limites_logicos_de_entrada() {
        val nivelFueraDeRangoSpO2 = Reglas.nivelSalud(
            saturacion = 40,
            sistolica = 120,
            diastolica = 80
        )
        assertEquals(NivelSalud.CRITICA, nivelFueraDeRangoSpO2)
    }

    @Test
    fun resultado_retorna_Requiere_revision_cuando_salud_es_CRITICA() {
        val items = listOf(ItemChecklist("1", "Mangueras", EstadoItem.CUMPLE, esCritico = true))
        val res = Reglas.resultado(NivelSalud.CRITICA, items)
        assertEquals("Requiere revisión", res)
    }

    @Test
    fun resultado_retorna_Requiere_revision_cuando_un_item_critico_TIENE_estado_NO_CUMPLE() {
        val items = listOf(
            ItemChecklist("1", "Oxígeno Principal", EstadoItem.NO_CUMPLE, esCritico = true)
        )
        val res = Reglas.resultado(NivelSalud.NORMAL, items)
        assertEquals("Requiere revisión", res)
    }

    @Test
    fun resultado_retorna_Observado_cuando_salud_es_ATENCION_o_hay_observaciones_en_checklist() {
        val items = listOf(
            ItemChecklist("1", "Luces de casco", EstadoItem.OBSERVADO, esCritico = false)
        )
        val res = Reglas.resultado(NivelSalud.NORMAL, items)
        assertEquals("Observado", res)
    }

    @Test
    fun resultado_retorna_Cumple_cuando_salud_es_NORMAL_y_todo_el_checklist_cumple() {
        val items = listOf(
            ItemChecklist("1", "Regulador", EstadoItem.CUMPLE, esCritico = true),
            ItemChecklist("2", "Aletas", EstadoItem.CUMPLE, esCritico = false)
        )
        val res = Reglas.resultado(NivelSalud.NORMAL, items)
        assertEquals("Cumple", res)
    }
}
