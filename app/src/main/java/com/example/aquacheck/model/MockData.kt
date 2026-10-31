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
        ItemChecklist(id = "1", nombre = "Equipamiento", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "2", nombre = "Compresor", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "3", nombre = "Oxígeno", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "4", nombre = "Señalización", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "5", nombre = "Buzo de emergencia", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "6", nombre = "Condiciones del entorno", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "7", nombre = "Equipamiento del buzo", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "8", nombre = "Compresor disponible y operativo", EstadoItem.CUMPLE),
        ItemChecklist(id = "9", nombre = "Oxígeno de contingencia", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "10", nombre = "Buzo de emergencia disponible", estado = EstadoItem.CUMPLE),
        ItemChecklist(id = "11", nombre = "Señalización visible para embarcaciones", esCritico = false),
        ItemChecklist(id = "12", nombre = "Condiciones del entorno", esCritico = false)
    )

    val defaultEvidencias = emptyList<EvidenciaItem>()

    val mockPreChequeos = emptyList<PreChequeo>()
}
