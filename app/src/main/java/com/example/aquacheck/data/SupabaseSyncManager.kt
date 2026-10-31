package com.example.aquacheck.data

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import java.io.File

@Serializable
data class PreChequeoSupabaseDto(
    val id: String,
    @SerialName("buzo_id") val buzoId: String,
    @SerialName("buzo_nombre") val buzoNombre: String,
    @SerialName("buzo_certificacion") val buzoCertificacion: String = "",
    @SerialName("supervisor_id") val supervisorId: String,
    @SerialName("supervisor_nombre") val supervisorNombre: String,
    @SerialName("centro_id") val centroId: String,
    @SerialName("centro_nombre") val centroNombre: String,
    @SerialName("centro_ubicacion") val centroUbicacion: String = "",
    val fecha: String,
    val resultado: String,
    val observaciones: String = "",
    @SerialName("frecuencia_cardiaca") val frecuenciaCardiaca: Int = 0,
    val sistolica: Int = 0,
    val diastolica: Int = 0,
    val saturacion: Int = 0,
    @SerialName("horas_sueno") val horasSueno: Int = 0,
    @SerialName("horas_ultimo_alcohol") val horasUltimoAlcohol: Int = 0,
    @SerialName("tiene_congestion_nasal") val tieneCongestionNasal: Boolean = false,
    @SerialName("toma_medicamentos_nuevos") val tomaMedicamentosNuevos: Boolean = false,
    @SerialName("estado_psicologico_calmo") val estadoPsicologicoCalmo: Boolean = true,
    @SerialName("evidencias_json") val evidenciasJson: JsonElement? = null
)

class SupabaseSyncManager(private val dao: PreChequeoDao) {

    private fun extractJsonString(element: JsonElement?): String {
        if (element == null) return "[]"
        return if (element is JsonPrimitive && element.isString) {
            element.content
        } else {
            element.toString()
        }
    }

    suspend fun sincronizar(): Result<Int> {
        return try {
            val pendientes = dao.getPreChequeosPendientes()
            var count = 0

            for (entity in pendientes) {
                val evidenciasLocales = PreChequeoEntity.toEvidenciasList(entity.evidenciasJson)
                val evidenciasRemotas = evidenciasLocales.map { evidencia ->
                    if (evidencia.imagePath != null && File(evidencia.imagePath).exists()) {
                        try {
                            val file = File(evidencia.imagePath)
                            val fileName = "${entity.id}_${evidencia.id}.jpg"
                            val bucket = SupabaseClientConfig.storage.from("evidencias-fotos")
                            bucket.upload(fileName, file.readBytes()) {
                                upsert = true
                            }
                            val publicUrl = bucket.publicUrl(fileName)
                            evidencia.copy(imagePath = publicUrl)
                        } catch (e: Exception) {
                            evidencia
                        }
                    } else {
                        evidencia
                    }
                }

                val evidenciasJsonRemoto = PreChequeoEntity.fromEvidenciasList(evidenciasRemotas)
                val jsonElement = try {
                    Json.parseToJsonElement(evidenciasJsonRemoto)
                } catch (e: Exception) {
                    Json.parseToJsonElement("[]")
                }

                val dto = PreChequeoSupabaseDto(
                    id = entity.id,
                    buzoId = entity.buzoId,
                    buzoNombre = entity.buzoNombre,
                    buzoCertificacion = entity.buzoCertificacion,
                    supervisorId = entity.supervisorId,
                    supervisorNombre = entity.supervisorNombre,
                    centroId = entity.centroId,
                    centroNombre = entity.centroNombre,
                    centroUbicacion = entity.centroUbicacion,
                    fecha = entity.fecha,
                    resultado = entity.resultado,
                    observaciones = entity.observaciones,
                    frecuenciaCardiaca = entity.frecuenciaCardiaca,
                    sistolica = entity.sistolica,
                    diastolica = entity.diastolica,
                    saturacion = entity.saturacion,
                    horasSueno = entity.horasSueno,
                    horasUltimoAlcohol = entity.horasUltimoAlcohol,
                    tieneCongestionNasal = entity.tieneCongestionNasal,
                    tomaMedicamentosNuevos = entity.tomaMedicamentosNuevos,
                    estadoPsicologicoCalmo = entity.estadoPsicologicoCalmo,
                    evidenciasJson = jsonElement
                )

                SupabaseClientConfig.postgrest.from("prechequeos").upsert(dto)
                dao.actualizarEvidenciasYSincronizado(entity.id, evidenciasJsonRemoto)
                count++
            }

            val remotos = SupabaseClientConfig.postgrest.from("prechequeos")
                .select()
                .decodeList<PreChequeoSupabaseDto>()

            for (r in remotos) {
                val evidenciasStr = extractJsonString(r.evidenciasJson)
                val entityLocal = PreChequeoEntity(
                    id = r.id,
                    buzoId = r.buzoId,
                    buzoNombre = r.buzoNombre,
                    buzoCertificacion = r.buzoCertificacion,
                    supervisorId = r.supervisorId,
                    supervisorNombre = r.supervisorNombre,
                    centroId = r.centroId,
                    centroNombre = r.centroNombre,
                    centroUbicacion = r.centroUbicacion,
                    fecha = r.fecha,
                    resultado = r.resultado,
                    observaciones = r.observaciones,
                    frecuenciaCardiaca = r.frecuenciaCardiaca,
                    sistolica = r.sistolica,
                    diastolica = r.diastolica,
                    saturacion = r.saturacion,
                    horasSueno = r.horasSueno,
                    horasUltimoAlcohol = r.horasUltimoAlcohol,
                    tieneCongestionNasal = r.tieneCongestionNasal,
                    tomaMedicamentosNuevos = r.tomaMedicamentosNuevos,
                    estadoPsicologicoCalmo = r.estadoPsicologicoCalmo,
                    evidenciasJson = evidenciasStr,
                    sincronizado = true
                )
                dao.insertPreChequeo(entityLocal)
            }

            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
