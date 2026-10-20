# Plan de Trabajo - Evaluación Parcial 2 (AquaCheck)

Completar los requerimientos pendientes de la auditoría para alcanzar el 100% de cumplimiento en la Evaluación Parcial 2 de VcM.

## User Review Required

- **Persistencia con Room**: Se implementará base de datos Room para guardar los pre-chequeos localmente en SQLite, reemplazando la lista en memoria.
- **Búsqueda y Filtros**: Se añadirá un buscador y filtros por estado en el historial de pre-chequeos.
- **Validación de Formularios**: Se reforzarán las validaciones en la selección del pre-chequeo.

## Proposed Changes

### Componente de Persistencia (Room)
#### [NEW] [AppDatabase.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/data/AppDatabase.kt)
#### [NEW] [PreChequeoDao.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/data/PreChequeoDao.kt)
#### [NEW] [PreChequeoEntity.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/data/PreChequeoEntity.kt)

### Componente de Lógica de Negocio y ViewModel
#### [MODIFY] [PreChequeoViewModel.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/viewmodel/PreChequeoViewModel.kt)

### Componente de Interfaz de Usuario (Búsqueda, Filtros y Formularios)
#### [MODIFY] [InicioScreen.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/ui/screens/InicioScreen.kt)
#### [MODIFY] [PreChequeoScreen.kt](file:///C:/Users/Y409-PCXX/StudioProjects/AquaCheck/app/src/main/java/com/example/aquacheck/ui/screens/PreChequeoScreen.kt)

## Verification Plan

### Automated Tests
- Ejecutar tests unitarios existentes (`LoginTest.kt`) y agregar pruebas para el DAO/ViewModel.
### Manual Verification
- Desplegar en emulador/dispositivo, crear un pre-chequeo, verificar que se persista tras cerrar/reabrir la app, probar la barra de búsqueda y filtros.
