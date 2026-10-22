package com.example.aquacheck.model

object MockData {
    val mockUsuarios = listOf(
        Usuario(id = "u1", nombre = "Carlos Mendoza", username = "supervisor", rol = RolUsuario.SUPERVISOR),
        Usuario(id = "u2", nombre = "Juan Jefe", username = "jefe", rol = RolUsuario.JEFE)
    )

    val mockBuzos = listOf(
        Buzo(id = "b1", nombre = "Juan Pérez", certificacion = "Matrícula Comercial Nivel 2"),
        Buzo(id = "b2", nombre = "Ana Gómez", certificacion = "Buceo Profesional Nivel 1"),
        Buzo(id = "b3", nombre = "Pedro Morales", certificacion = "Matrícula Comercial Nivel 3"),
        Buzo(id = "b4", nombre = "Diego Torres", certificacion = "Buceo Industrial Avanzado")
    )

    val mockSupervisores = listOf(
        Supervisor(id = "s1", nombre = "Carlos Mendoza"),
        Supervisor(id = "s2", nombre = "Roberto Silva"),
        Supervisor(id = "s3", nombre = "Mariana Fuentes")
    )

    val mockCentros = listOf(
        Centro(id = "c1", nombre = "Centro Chiloé", ubicacion = "Seno Reloncaví, Puerto Montt"),
        Centro(id = "c2", nombre = "Centro Calbuco", ubicacion = "Canal Calbuco, Región de Los Lagos"),
        Centro(id = "c3", nombre = "Centro Aysén", ubicacion = "Fiordo Aysén, XI Región"),
        Centro(id = "c4", nombre = "Centro Melinka", ubicacion = "Archipiélago de Las Guaitecas")
    )

    val defaultChecklistItems = listOf(
        ItemChecklist(id = "1", nombre = "Equipamiento del buzo", estado = EstadoItem.CUMPLE, esCritico = true),
        ItemChecklist(id = "2", nombre = "Compresor disponible y operativo", estado = EstadoItem.CUMPLE, esCritico = true),
        ItemChecklist(id = "3", nombre = "Oxígeno de contingencia", estado = EstadoItem.CUMPLE, esCritico = true),
        ItemChecklist(id = "4", nombre = "Buzo de emergencia disponible", estado = EstadoItem.CUMPLE, esCritico = true),
        ItemChecklist(id = "5", nombre = "Señalización visible para embarcaciones", estado = EstadoItem.CUMPLE, esCritico = false),
        ItemChecklist(id = "6", nombre = "Condiciones del entorno", estado = EstadoItem.CUMPLE, esCritico = false)
    )

    val defaultEvidencias = emptyList<EvidenciaItem>()

    val mockPreChequeos = listOf(
        PreChequeo(
            id = "pc1",
            buzo = mockBuzos[0],
            supervisor = mockSupervisores[0],
            centro = mockCentros[0],
            fecha = "2026-10-04",
            items = defaultChecklistItems,
            evidencias = defaultEvidencias,
            resultado = "Apto"
        ),
        PreChequeo(
            id = "pc2",
            buzo = mockBuzos[1],
            supervisor = mockSupervisores[1],
            centro = mockCentros[1],
            fecha = "2026-10-03",
            items = defaultChecklistItems.mapIndexed { index, item ->
                if (index == 2) item.copy(estado = EstadoItem.OBSERVADO) else item
            },
            evidencias = defaultEvidencias.take(2),
            resultado = "Observado"
        )
    )
}
