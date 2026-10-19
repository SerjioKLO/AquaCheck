# Project Plan

AquaCheck Buceo - App Android nativa en Kotlin + Jetpack Compose + Material Design 3 (Hito Académico).
Demuestra arquitectura MVVM, Material Design 3 y Navigation Compose con datos ficticios hardcodeados (sin Room, DataStore, API REST, cámara real ni autenticación real).

Estructura de carpetas requerida:
- model/ (Usuario, Buzo, Supervisor, Centro, ItemChecklist, EstadoItem enum, PreChequeo, Resultado)
- viewmodel/ (PreChequeoViewModel con StateFlow<PreChequeoUiState> y lógica de negocio para determinar "Apto" u "Observado")
- ui/screens/ (LoginScreen, InicioScreen, PreChequeoScreen, ChecklistScreen, ResumenScreen)

Sistema de diseño (M3 Tokens Exactos):
- Principal: #12260E
- Secundario/Contenedor: #D6E8CF
- Fondo: #F6F8F4
- Texto principal: #1C1C1C
- Alerta/Error: #BA1A1A
- Contenedor de alerta: #FFEDEA
- Botones de acción: pill (totalmente redondeados, fondo #12260E, texto blanco)

Navegación declarativa (Navigation Compose):
- Rutas: "login", "inicio", "prechequeo", "checklist/{id}", "resumen"

## Project Brief

# Project Brief: AquaCheck Buceo

AquaCheck Buceo es una aplicación móvil nativa para Android enfocada en la gestión y validación de pre-chequeos de seguridad para operaciones de buceo profesional (Hito Académico).

---

## Features

1. **Autenticación Simulada (Login):** Pantalla de acceso simple con selección de usuarios y roles preconfigurados (Buzo y Supervisor).
2. **Panel de Inicio:** Pantalla principal para revisar el estado general del buzo y comenzar un nuevo protocolo de pre-chequeo.
3. **Checklist de Pre-Chequeo Interactivo:** Formulario interactivo por items de seguridad para evaluar el equipo con selección de estados (`Apto`, `Observado`, `No Aplica`).
4. **Resumen y Dictamen Automático:** Vista final con dictamen automático del estado operativo (`Apto` u `Observado`) calculado según la evaluación del checklist.

---

## High-Level Tech Stack

- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose + Material Design 3 (con tokens exactos: `#12260E` Principal, `#D6E8CF` Contenedor, `#F6F8F4` Fondo, `#1C1C1C` Texto, `#BA1A1A` Alerta, `#FFEDEA` Contenedor Alerta)
- **Navegación:** Jetpack Navigation 3 (State-Driven Navigation)
- **Layout Adaptativo:** Compose Material Adaptive library
- **Arquitectura:** MVVM (Model-View-ViewModel) con `StateFlow` (`PreChequeoUiState`) y Kotlin Coroutines
- **Datos:** In-Memory Mock Data (datos ficticios hardcodeados sin persistencia de base de datos ni API REST)

## Implementation Steps
**Total Duration:** 2h 33m 51s

### Task_1_Theme_Models_ViewModel: Define Material Design 3 theme with exact AquaCheck color tokens (#12260E, #D6E8CF, #F6F8F4, #1C1C1C, #BA1A1A, #FFEDEA), create domain data models (Usuario, Buzo, Supervisor, Centro, EstadoItem, ItemChecklist, PreChequeo, Resultado) with mock data in model/, and implement PreChequeoViewModel with PreChequeoUiState and pure calculation logic for 'Apto' vs 'Observado' result.
- **Status:** COMPLETED
- **Updates:** Theme tokens, data models in model/, and PreChequeoViewModel in viewmodel/ created successfully with pure calculation logic and verified build.
- **Acceptance Criteria:**
  - M3 Theme with exact AquaCheck color tokens created
  - Data models in model/ package defined with mock data
  - PreChequeoViewModel with calculation logic and StateFlow implemented
  - Project builds successfully
- **Duration:** 6m 28s

### Task_2_Screens_And_Navigation: Implement Jetpack Navigation Compose graph with routes ('login', 'inicio', 'prechequeo', 'checklist/{id}', 'resumen') using shared PreChequeoViewModel, and build all 5 UI screens: LoginScreen, InicioScreen, PreChequeoScreen, ChecklistScreen, and ResumenScreen.
- **Status:** COMPLETED
- **Updates:** All 5 UI screens (LoginScreen matching Logo.svg design, InicioScreen, PreChequeoScreen, ChecklistScreen, ResumenScreen) and AppNavigation graph implemented with shared ViewModel and verified build.
- **Acceptance Criteria:**
  - Navigation Host set up with routes login, inicio, prechequeo, checklist/{id}, and resumen
  - LoginScreen, InicioScreen, PreChequeoScreen, ChecklistScreen, and ResumenScreen implemented and styled with M3 tokens
  - Interactive pre-check checklist evaluation works and displays calculated Apto/Observado result in ResumenScreen
  - Project builds successfully
- **Duration:** 2h 6m 4s

### Task_3_Run_And_Verify: Perform full app build, run verification, and instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Verification completed. App builds cleanly, all 5 screens and MVVM navigation flow verified, exact M3 color tokens validated, and LoginScreen title updated to 'AQUACHECK BUCEO'.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - critic_agent verifies application stability, user requirements alignment, and UI consistency
- **Duration:** 21m 19s

