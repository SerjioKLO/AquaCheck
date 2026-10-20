package com.example.aquacheck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.aquacheck.model.Buzo
import com.example.aquacheck.model.Centro
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import com.example.aquacheck.model.Supervisor

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
    val observaciones: String
) {
    fun toDomain(): PreChequeo {
        return PreChequeo(
            id = id,
            buzo = Buzo(id = buzoId, nombre = buzoNombre, certificacion = buzoCertificacion),
            supervisor = Supervisor(id = supervisorId, nombre = supervisorNombre),
            centro = Centro(id = centroId, nombre = centroNombre, ubicacion = centroUbicacion),
            fecha = fecha,
            items = MockData.defaultChecklistItems,
            evidencias = emptyList(),
            resultado = resultado,
            observaciones = observaciones
        )
    }

    companion object {
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
                observaciones = preChequeo.observaciones
            )
        }
    }
}
