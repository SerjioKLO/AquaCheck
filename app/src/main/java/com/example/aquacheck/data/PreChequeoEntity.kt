package com.example.aquacheck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.DatosSalud
import com.example.aquacheck.model.EvidenciaItem
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.Supervisor
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@Entity(tableName = "prechequeos")
data class PreChequeoEntity(
    @PrimaryKey
    val id: String,
    val buzoId: String,
    val buzoNombre: String,
    val buzoCertificacion: String,
    val supervisorId: String,
    val supervisorNombre: String,
    val centroId: String,
    val centroNombre: String,
    val centroUbicacion: String,
    val fecha: String,
    val resultado: String,
    val observaciones: String,
    val frecuenciaCardiaca: Int = 0,
    val sistolica: Int = 0,
    val diastolica: Int = 0,
    val saturacion: Int = 0,
    val horasSueno: Int = 0,
    val horasUltimoAlcohol: Int = 0,
    val tieneCongestionNasal: Boolean = false,
    val tomaMedicamentosNuevos: Boolean = false,
    val estadoPsicologicoCalmo: Boolean = true,
    val evidenciasJson: String = "",
    val sincronizado: Boolean = false
) {
    fun toDomain(): PreChequeo {
        val evidenciasList = toEvidenciasList(evidenciasJson)
        return PreChequeo(
            id = id,
            buzo = Buzo(id = buzoId, nombre = buzoNombre, certificacion = buzoCertificacion),
            supervisor = Supervisor(id = supervisorId, nombre = supervisorNombre),
            centro = Centro(id = centroId, nombre = centroNombre, ubicacion = centroUbicacion),
            fecha = fecha,
            items = MockData.defaultChecklistItems,
            evidencias = evidenciasList,
            resultado = resultado,
            observaciones = observaciones,
            frecuenciaCardiaca = frecuenciaCardiaca,
            sistolica = sistolica,
            diastolica = diastolica,
            saturacion = saturacion,
            horasSueno = horasSueno,
            horasUltimoAlcohol = horasUltimoAlcohol,
            tieneCongestionNasal = tieneCongestionNasal,
            tomaMedicamentosNuevos = tomaMedicamentosNuevos,
            estadoPsicologicoCalmo = estadoPsicologicoCalmo,
            salud = DatosSalud(
                frecuenciaCardiaca = frecuenciaCardiaca,
                sistolica = sistolica,
                diastolica = diastolica,
                saturacion = saturacion,
                horasSueno = horasSueno,
                horasUltimoAlcohol = horasUltimoAlcohol,
                tieneCongestionNasal = tieneCongestionNasal,
                tomaMedicamentosNuevos = tomaMedicamentosNuevos,
                estadoPsicologicoCalmo = estadoPsicologicoCalmo,
                presionsistolica = sistolica,
                presionDiastolica = diastolica,
                saturacionOxigeno = saturacion
            )
        )
    }

    companion object {
        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        private val listType = Types.newParameterizedType(List::class.java, EvidenciaDto::class.java)
        private val adapter = moshi.adapter<List<EvidenciaDto>>(listType)

        fun toEvidenciasList(json: String): List<EvidenciaItem> {
            if (json.isBlank()) return emptyList()
            return try {
                val dtos = adapter.fromJson(json)
                dtos?.map { EvidenciaItem(id = it.id, titulo = it.titulo, imagePath = it.imagePath) } ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun fromEvidenciasList(list: List<EvidenciaItem>): String {
            return try {
                val dtos = list.map { EvidenciaDto(id = it.id, titulo = it.titulo, imagePath = it.imagePath) }
                adapter.toJson(dtos)
            } catch (e: Exception) {
                ""
            }
        }

        fun fromDomain(preChequeo: PreChequeo): PreChequeoEntity {
            return PreChequeoEntity(
                id = preChequeo.id,
                buzoId = preChequeo.buzo.id,
                buzoNombre = preChequeo.buzo.nombre,
                buzoCertificacion = preChequeo.buzo.certificacion,
                supervisorId = preChequeo.supervisor.id,
                supervisorNombre = preChequeo.supervisor.nombre,
                centroId = preChequeo.centro.id,
                centroNombre = preChequeo.centro.nombre,
                centroUbicacion = preChequeo.centro.ubicacion,
                fecha = preChequeo.fecha,
                resultado = preChequeo.resultado,
                observaciones = preChequeo.observaciones,
                frecuenciaCardiaca = preChequeo.frecuenciaCardiaca,
                sistolica = preChequeo.sistolica,
                diastolica = preChequeo.diastolica,
                saturacion = preChequeo.saturacion,
                horasSueno = preChequeo.horasSueno,
                horasUltimoAlcohol = preChequeo.horasUltimoAlcohol,
                tieneCongestionNasal = preChequeo.tieneCongestionNasal,
                tomaMedicamentosNuevos = preChequeo.tomaMedicamentosNuevos,
                estadoPsicologicoCalmo = preChequeo.estadoPsicologicoCalmo,
                evidenciasJson = fromEvidenciasList(preChequeo.evidencias),
                sincronizado = false
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class EvidenciaDto(
    val id: String,
    val titulo: String,
    val imagePath: String?
)
