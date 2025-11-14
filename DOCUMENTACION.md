# 📚 Documentación Completa - AsisteDocente

## 📱 Información General del Proyecto

**Nombre**: AsisteDocente (AsistenciaDocente / AttendanceTeacher)
**Versión**: 1.0
**Package**: `sv.edu.catolica.asistedocente`
**Plataforma**: Android (Nativo)
**Lenguajes**: Kotlin + Java
**Min SDK**: 26 (Android 8.0 Oreo)
**Target SDK**: 34 (Android 14)

### 🎯 Propósito

AsisteDocente es una aplicación móvil diseñada para facilitar la gestión de asistencia de estudiantes por parte de docentes. Permite registrar asistencia de forma rápida durante las clases, mantener un historial completo, y generar reportes profesionales en formato PDF y Excel.

### 🎥 Referencias de Diseño

- **Video principal**: https://streamable.com/hya7nq (referencia PRIORITARIA)
- **Prototipo Canva**: https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit

**Nota importante**: En caso de conflicto entre referencias, el video tiene prioridad absoluta.

---

## 🏗️ Arquitectura del Proyecto

### Patrón Arquitectónico: MVVM (Model-View-ViewModel)

La aplicación utiliza el patrón MVVM para mantener una separación clara de responsabilidades:

```
┌─────────────────────────────────────────────┐
│          UI Layer (View)                    │
│  • Composables (Jetpack Compose)            │
│  • Material 3 Components                    │
│  • Navigation                               │
└─────────────────┬───────────────────────────┘
                  │ Observa StateFlow
                  ▼
┌─────────────────────────────────────────────┐
│        ViewModel Layer                      │
│  • Maneja UI State                          │
│  • Lógica de presentación                   │
│  • Transformación de datos                  │
└─────────────────┬───────────────────────────┘
                  │ Llama métodos
                  ▼
┌─────────────────────────────────────────────┐
│        Repository Layer                     │
│  • Single Source of Truth                   │
│  • Abstrae fuentes de datos                 │
│  • Lógica de negocio                        │
└─────────────────┬───────────────────────────┘
                  │ Usa DAOs
                  ▼
┌─────────────────────────────────────────────┐
│        Data Layer (Model)                   │
│  • Room Database (SQLite)                   │
│  • Entities y DAOs                          │
│  • Type Converters                          │
└─────────────────────────────────────────────┘
```

### Stack Tecnológico

#### Core
- **Kotlin**: 1.9.22 - Lenguaje principal
- **Java**: 17 - Target JVM
- **Gradle**: 8.2.2 con Groovy DSL
- **AGP**: 8.2.2 (Android Gradle Plugin)

#### UI Framework
- **Jetpack Compose**: 1.6.0 - Framework UI declarativo
- **Material 3**: 1.2.0 - Design system
- **Navigation Compose**: 2.7.6 - Navegación entre pantallas
- **Coil**: 2.5.0 - Carga de imágenes

#### Persistencia
- **Room Database**: 2.6.1 - ORM para SQLite
- **DataStore Preferences**: 1.0.0 - Preferencias de app
- **SQLite**: Base de datos local

#### Inyección de Dependencias
- **Hilt (Dagger)**: 2.50 - DI framework

#### Asincronía
- **Kotlin Coroutines**: 1.7.3 - Programación asíncrona
- **Flow**: Streams reactivos
- **LiveData**: Observables (legacy)

#### Lifecycle
- **ViewModel**: 2.7.0 - Manejo de estado UI
- **Lifecycle Runtime**: 2.7.0 - Ciclo de vida

#### Reportes
- **iText7**: 7.2.5 - Generación de PDFs
- **Apache POI**: 5.2.3 - Generación de Excel

#### Background Tasks
- **WorkManager**: 2.9.0 - Tareas en background

#### Testing
- **JUnit**: 4.13.2 - Unit testing
- **AndroidX Test**: 1.1.5 - Instrumentation testing
- **Coroutines Test**: 1.7.3 - Testing async code

---

## 📊 Base de Datos (Room)

### Diagrama ER

```
┌─────────────────┐
│    Docente      │
│─────────────────│
│ id (PK)         │
│ nombre          │
│ apellido        │
│ email           │
│ telefono        │
│ foto            │
│ activo          │
│ fechaCreacion   │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────▼────────┐
│     Grupo       │
│─────────────────│
│ id (PK)         │
│ nombre          │
│ materia         │
│ horario         │
│ descripcion     │
│ docenteId (FK)  │
│ activo          │
│ fechaCreacion   │
└────────┬────────┘
         │ 1
         │
         │ N
┌────────▼────────────┐         ┌─────────────────────┐
│    Estudiante       │    N    │ RegistroAsistencia  │
│─────────────────────│◄────────│─────────────────────│
│ id (PK)             │         │ id (PK)             │
│ nombre              │         │ estudianteId (FK)   │
│ apellido            │         │ grupoId (FK)        │
│ codigo (UNIQUE)     │         │ fecha               │
│ email               │         │ estado              │
│ foto                │         │ notas               │
│ grupoId (FK)        │         │ horaRegistro        │
│ activo              │         └─────────────────────┘
│ fechaCreacion       │
└─────────────────────┘
```

### Entidades Detalladas

#### 1. Docente
**Tabla**: `docentes`

Almacena información de los docentes que usan la aplicación.

**Campos**:
- `id` (Long, PK, AutoIncrement): Identificador único
- `nombre` (String): Nombre del docente
- `apellido` (String): Apellido del docente
- `email` (String): Correo electrónico
- `telefono` (String?, Nullable): Teléfono de contacto
- `foto` (String?, Nullable): URI de foto de perfil
- `activo` (Boolean, Default: true): Estado activo/inactivo
- `fechaCreacion` (Long): Timestamp de creación

**Índices**:
- `email`: Índice único para búsquedas rápidas

**Relaciones**:
- `1:N` con Grupo (un docente puede tener múltiples grupos)

#### 2. Grupo
**Tabla**: `grupos`

Representa un grupo o clase de estudiantes.

**Campos**:
- `id` (Long, PK, AutoIncrement): Identificador único
- `nombre` (String): Nombre del grupo (ej: "Matemáticas 2024-A")
- `materia` (String): Materia impartida
- `horario` (String?, Nullable): Horario de clases
- `descripcion` (String?, Nullable): Descripción adicional
- `docenteId` (Long, FK): Referencia al docente
- `activo` (Boolean, Default: true): Estado activo/inactivo
- `fechaCreacion` (Long): Timestamp de creación

**Foreign Keys**:
- `docenteId` → `docentes(id)` ON DELETE CASCADE

**Índices**:
- `docenteId`: Para consultas por docente
- `activo`: Para filtrar grupos activos

**Relaciones**:
- `N:1` con Docente
- `1:N` con Estudiante
- `1:N` con RegistroAsistencia

#### 3. Estudiante
**Tabla**: `estudiantes`

Información de los estudiantes registrados.

**Campos**:
- `id` (Long, PK, AutoIncrement): Identificador único
- `nombre` (String): Nombre del estudiante
- `apellido` (String): Apellido del estudiante
- `codigo` (String, UNIQUE): Código/matrícula único
- `email` (String?, Nullable): Correo electrónico
- `foto` (String?, Nullable): URI de foto de perfil
- `grupoId` (Long, FK): Grupo al que pertenece
- `activo` (Boolean, Default: true): Estado activo/inactivo
- `fechaCreacion` (Long): Timestamp de creación

**Foreign Keys**:
- `grupoId` → `grupos(id)` ON DELETE CASCADE

**Índices**:
- `grupoId`: Para consultas por grupo
- `codigo` (UNIQUE): Garantiza códigos únicos
- `activo`: Para filtrar estudiantes activos

**Relaciones**:
- `N:1` con Grupo
- `1:N` con RegistroAsistencia

#### 4. RegistroAsistencia
**Tabla**: `registros_asistencia`

Registros individuales de asistencia de estudiantes.

**Campos**:
- `id` (Long, PK, AutoIncrement): Identificador único
- `estudianteId` (Long, FK): Estudiante que registra
- `grupoId` (Long, FK): Grupo asociado
- `fecha` (Long): Timestamp del día (normalizado a 00:00:00)
- `estado` (EstadoAsistencia): PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO
- `notas` (String?, Nullable): Notas adicionales
- `horaRegistro` (Long): Timestamp exacto del registro

**Foreign Keys**:
- `estudianteId` → `estudiantes(id)` ON DELETE CASCADE
- `grupoId` → `grupos(id)` ON DELETE CASCADE

**Índices**:
- Índice único compuesto: `(estudianteId, fecha)` - Previene registros duplicados
- `grupoId, fecha`: Para consultas por grupo y fecha
- `fecha`: Para consultas por rango de fechas

**Constraints**:
- UNIQUE(estudianteId, fecha): Un estudiante no puede tener más de un registro por día

**Relaciones**:
- `N:1` con Estudiante
- `N:1` con Grupo

#### 5. EstadoAsistencia (Enum)
**Valores posibles**:
```kotlin
enum class EstadoAsistencia {
    PRESENTE,      // Estudiante presente
    AUSENTE,       // Estudiante ausente
    TARDANZA,      // Estudiante llegó tarde
    JUSTIFICADO    // Ausencia justificada
}
```

**Type Converter**: Convierte automáticamente entre String y Enum para almacenamiento en BD.

### DAOs (Data Access Objects)

Cada entidad tiene su propio DAO con queries optimizadas:

#### DocenteDao
- `insert()`: Insertar docente
- `update()`: Actualizar docente
- `delete()`: Eliminar docente
- `getDocenteById()`: Obtener por ID
- `getAllDocentes()`: Listar todos
- `getDocentesActivos()`: Filtrar activos
- `getDocenteByEmail()`: Buscar por email
- `countDocentes()`: Contar total

#### GrupoDao
- `insert()`: Insertar grupo
- `update()`: Actualizar grupo
- `delete()`: Eliminar grupo
- `getGrupoById()`: Obtener por ID
- `getAllGrupos()`: Listar todos (Flow)
- `getGruposActivos()`: Filtrar activos (Flow)
- `getGruposPorDocente()`: Filtrar por docente (Flow)
- `countEstudiantesPorGrupo()`: Contar estudiantes
- `searchGrupos()`: Búsqueda por texto
- `deactivateGrupo()`: Desactivar (soft delete)

#### EstudianteDao
- `insert()`: Insertar estudiante
- `update()`: Actualizar estudiante
- `delete()`: Eliminar estudiante
- `getEstudianteById()`: Obtener por ID
- `getAllEstudiantes()`: Listar todos
- `getEstudiantesPorGrupo()`: Filtrar por grupo (Flow)
- `getEstudiantesActivosPorGrupo()`: Filtrar activos de grupo (Flow)
- `getEstudianteByCodigo()`: Buscar por código
- `searchEstudiantes()`: Búsqueda por texto
- `countEstudiantesPorGrupo()`: Contar por grupo
- `transferirGrupo()`: Cambiar estudiante de grupo
- `deactivateEstudiante()`: Desactivar (soft delete)

#### RegistroAsistenciaDao
- `insert()`: Insertar registro (con manejo de duplicados)
- `update()`: Actualizar registro
- `delete()`: Eliminar registro
- `getRegistroById()`: Obtener por ID
- `getAllRegistros()`: Listar todos
- `getRegistrosPorGrupo()`: Filtrar por grupo (Flow)
- `getRegistrosPorEstudiante()`: Filtrar por estudiante (Flow)
- `getRegistrosPorGrupoYFecha()`: Filtrar por grupo y fecha (Flow)
- `getRegistrosPorFecha()`: Filtrar por fecha
- `getRegistrosEnRango()`: Filtrar por rango de fechas
- `getUltimoRegistroPorEstudiante()`: Último registro de estudiante
- `existeRegistro()`: Verificar si existe registro
- `countPorEstado()`: Contar por estado de asistencia
- `getPorcentajeAsistencia()`: Calcular % de asistencia
- `deleteRegistrosPorGrupo()`: Eliminar registros de grupo
- `getEstadisticasPorGrupo()`: Obtener estadísticas consolidadas

**Nota sobre Queries**: Todas las queries que retornan listas usan `Flow` para reactividad, permitiendo que la UI se actualice automáticamente cuando cambian los datos.

### Type Converters

**Converters.kt**: Convierte tipos personalizados para almacenar en Room

```kotlin
class Converters {
    @TypeConverter
    fun fromEstadoAsistencia(value: EstadoAsistencia): String {
        return value.name
    }

    @TypeConverter
    fun toEstadoAsistencia(value: String): EstadoAsistencia {
        return EstadoAsistencia.valueOf(value)
    }
}
```

---

## 🔧 Capa de Repositorio

Los Repositories abstraen las fuentes de datos y aplican lógica de negocio.

### DocenteRepository

**Responsabilidad**: Gestión de docentes

**Métodos principales**:
- `getAllDocentes()`: Flow con todos los docentes
- `getDocenteById(id)`: Obtener docente específico
- `getDocentesActivos()`: Filtrar activos
- `insertDocente(docente)`: Crear nuevo docente
- `updateDocente(docente)`: Actualizar docente
- `deleteDocente(docente)`: Eliminar docente

**Lógica de negocio**: Validaciones básicas antes de insertar/actualizar

### GrupoRepository

**Responsabilidad**: Gestión de grupos/clases

**Métodos principales**:
- `getAllGrupos()`: Flow con todos los grupos
- `getGruposActivos()`: Flow con grupos activos
- `getGruposPorDocente(docenteId)`: Filtrar por docente
- `getGrupoById(id)`: Obtener grupo específico
- `insertGrupo(grupo)`: Crear nuevo grupo
- `updateGrupo(grupo)`: Actualizar grupo
- `deleteGrupo(grupo)`: Eliminar grupo
- `deactivateGrupo(id)`: Soft delete
- `countEstudiantesPorGrupo(id)`: Contar estudiantes

**Lógica de negocio**: Validación de datos requeridos, verificación de relaciones

### EstudianteRepository

**Responsabilidad**: Gestión de estudiantes

**Métodos principales**:
- `getAllEstudiantes()`: Listar todos
- `getEstudiantesPorGrupo(grupoId)`: Flow filtrado por grupo
- `getEstudiantesActivosPorGrupo(grupoId)`: Solo activos de un grupo
- `getEstudianteById(id)`: Obtener específico
- `getEstudianteByCodigo(codigo)`: Buscar por código
- `insertEstudiante(estudiante)`: Crear nuevo
- `updateEstudiante(estudiante)`: Actualizar
- `deleteEstudiante(estudiante)`: Eliminar
- `transferirGrupo(estudianteId, nuevoGrupoId)`: Cambiar de grupo
- `searchEstudiantes(query)`: Búsqueda por texto

**Lógica de negocio**:
- Validación de código único
- Verificación de grupo válido
- Normalización de nombres (capitalización)

### AsistenciaRepository

**Responsabilidad**: Gestión de registros de asistencia y estadísticas

**Métodos principales**:
- `insertRegistro(registro)`: Crear registro (previene duplicados)
- `updateRegistro(registro)`: Actualizar registro
- `deleteRegistro(registro)`: Eliminar registro
- `getRegistrosPorGrupoYFecha(grupoId, fecha)`: Flow de registros del día
- `getRegistrosPorEstudiante(estudianteId)`: Historial de estudiante
- `getRegistrosEnRango(grupoId, fechaInicio, fechaFin)`: Rango de fechas
- `existeRegistro(estudianteId, fecha)`: Verificar duplicado
- `getPorcentajeAsistencia(estudianteId, fechaInicio, fechaFin)`: Calcular %
- `getEstadisticasPorGrupo(grupoId, fechaInicio, fechaFin)`: Estadísticas consolidadas
- `countPorEstado(grupoId, estado, fechaInicio, fechaFin)`: Contar por estado

**Lógica de negocio**:
- Normalización de fechas (00:00:00 del día)
- Prevención de registros duplicados
- Cálculos de porcentajes y estadísticas
- Validación de fechas coherentes

---

## 🎨 Capa de UI (Jetpack Compose)

### Sistema de Navegación

**Screen.kt**: Define todas las rutas como sealed class

```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")

    object GroupList : Screen("groups")

    object GroupDetail : Screen("group/{groupId}") {
        fun createRoute(groupId: Long) = "group/$groupId"
    }

    object AddEditGroup : Screen("group/edit?groupId={groupId}") {
        fun createRoute(groupId: Long? = null) =
            if (groupId != null) "group/edit?groupId=$groupId"
            else "group/edit"
    }

    object StudentList : Screen("students/{groupId}") {
        fun createRoute(groupId: Long) = "students/$groupId"
    }

    object AddEditStudent : Screen("student/edit/{groupId}?studentId={studentId}") {
        fun createRoute(groupId: Long, studentId: Long? = null) =
            if (studentId != null) "student/edit/$groupId?studentId=$studentId"
            else "student/edit/$groupId"
    }

    object Attendance : Screen("attendance/{groupId}?date={date}") {
        fun createRoute(groupId: Long, date: Long? = null) =
            if (date != null) "attendance/$groupId?date=$date"
            else "attendance/$groupId"
    }

    object AttendanceHistory : Screen("attendance/history/{groupId}") {
        fun createRoute(groupId: Long) = "attendance/history/$groupId"
    }

    object Reports : Screen("reports")

    object ReportDetail : Screen("reports/{groupId}/{startDate}/{endDate}") {
        fun createRoute(groupId: Long, startDate: Long, endDate: Long) =
            "reports/$groupId/$startDate/$endDate"
    }
}
```

**NavGraph.kt**: Configura el grafo de navegación con Navigation Compose

- Define todas las rutas con sus argumentos
- Extrae argumentos del backstack
- Pasa lambdas de navegación a cada screen
- Integra ViewModels con `hiltViewModel()`

**Patrón de navegación**:
- NUNCA pasar `NavController` directamente a composables
- Usar lambdas para encapsular acciones de navegación
- Extraer argumentos en NavGraph, no en las screens

### Pantallas Implementadas

#### 1. HomeScreen
**Archivo**: `ui/screens/home/HomeScreen.kt`
**ViewModel**: `HomeViewModel.kt`

**Funcionalidad**:
- Dashboard principal de la aplicación
- Muestra lista de grupos activos
- Acceso rápido a tomar asistencia
- Estadísticas generales
- FAB para crear nuevo grupo

**UI States**:
```kotlin
sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(val grupos: List<Grupo>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

**Componentes**:
- `TopAppBar` con título y acciones
- `FloatingActionButton` para crear grupo
- `LazyColumn` con lista de `GrupoCard`
- Estados: Loading, Empty, Success, Error

**Navegación desde aquí**:
- Detalle de grupo
- Crear grupo
- Tomar asistencia
- Ver reportes
- Editar grupo

#### 2. GroupDetailScreen
**Archivo**: `ui/screens/groups/GroupDetailScreen.kt`
**ViewModel**: `GroupDetailViewModel.kt`

**Funcionalidad**:
- Muestra información completa del grupo
- Lista de estudiantes del grupo
- Botón destacado para tomar asistencia
- Opciones de edición y gestión

**UI States**:
```kotlin
sealed interface GroupDetailUiState {
    object Loading : GroupDetailUiState
    data class Success(
        val grupo: Grupo,
        val estudiantes: List<Estudiante>,
        val estadisticas: EstadisticasGrupo?
    ) : GroupDetailUiState
    data class Error(val message: String) : GroupDetailUiState
}
```

**Componentes**:
- Cabecera con info del grupo
- FAB para agregar estudiante
- `LazyColumn` con `EstudianteCard` por cada estudiante
- Botón prominente "Tomar Asistencia Hoy"

**Acciones disponibles**:
- Editar grupo
- Agregar estudiante
- Editar estudiante
- Eliminar estudiante
- Tomar asistencia
- Ver historial

#### 3. AddEditGroupScreen
**Archivo**: `ui/screens/groups/AddEditGroupScreen.kt`
**ViewModel**: `AddEditGroupViewModel.kt`

**Funcionalidad**:
- Crear nuevo grupo o editar existente
- Formulario con validaciones
- Guardado con feedback

**Campos del formulario**:
- Nombre del grupo (requerido)
- Materia (requerido)
- Horario (opcional)
- Descripción (opcional)

**Validaciones**:
- Campos requeridos no vacíos
- Longitud mínima de nombres
- Feedback visual de errores

**Estados del formulario**:
- `nombre: MutableState<String>`
- `materia: MutableState<String>`
- `horario: MutableState<String>`
- `descripcion: MutableState<String>`
- `isLoading: MutableState<Boolean>`
- `errorMessage: MutableState<String?>`

#### 4. AddEditStudentScreen
**Archivo**: `ui/screens/students/AddEditStudentScreen.kt`
**ViewModel**: `AddEditStudentViewModel.kt`

**Funcionalidad**:
- Crear nuevo estudiante o editar existente
- Formulario con validaciones
- Opción de foto (pendiente implementación completa)

**Campos del formulario**:
- Nombre (requerido)
- Apellido (requerido)
- Código/Matrícula (requerido, único)
- Email (opcional)
- Foto (opcional, pendiente)

**Validaciones**:
- Código único en toda la BD
- Email con formato válido
- Campos requeridos completos

#### 5. AttendanceScreen ⭐ (PANTALLA CRÍTICA)
**Archivo**: `ui/screens/attendance/AttendanceScreen.kt`
**ViewModel**: `AttendanceViewModel.kt`

**Funcionalidad**:
- Registro rápido de asistencia de estudiantes
- Optimizada para uso en el aula
- Guardado automático o manual

**UI States**:
```kotlin
sealed interface AttendanceUiState {
    object Loading : AttendanceUiState
    data class Success(
        val estudiantes: List<EstudianteConAsistencia>,
        val fecha: Long,
        val grupo: Grupo
    ) : AttendanceUiState
    data class Error(val message: String) : AttendanceUiState
}

data class EstudianteConAsistencia(
    val estudiante: Estudiante,
    val estadoActual: EstadoAsistencia?,
    val registroId: Long?
)
```

**Componentes**:
- Selector de fecha (DatePicker)
- `LazyColumn` con `EstudianteAttendanceCard`
- Cada card tiene 4 botones: Presente, Ausente, Tardanza, Justificado
- Botones con código de colores
- Guardado automático al cambiar estado

**Estados de asistencia con colores**:
- **PRESENTE**: Verde (MaterialTheme.colorScheme.primary)
- **AUSENTE**: Rojo (MaterialTheme.colorScheme.error)
- **TARDANZA**: Naranja/Amarillo (MaterialTheme.colorScheme.tertiary)
- **JUSTIFICADO**: Azul claro (MaterialTheme.colorScheme.secondary)

**Performance**:
- Carga optimizada con Flow
- Render eficiente con LazyColumn
- Debouncing en guardado automático
- Objetivo: cargar en <1 segundo

**Validaciones**:
- Constraint único en BD previene duplicados
- Feedback visual al guardar
- Manejo de errores

#### 6. ReportsScreen
**Archivo**: `ui/screens/reports/ReportsScreen.kt`
**ViewModel**: `ReportsViewModel.kt`

**Funcionalidad**:
- Visualización de estadísticas de asistencia
- Filtros por grupo, fecha, estudiante
- Exportación a PDF/Excel (pendiente implementación)

**Filtros disponibles**:
- Selector de grupo
- Rango de fechas (inicio - fin)
- Tipo de reporte (grupal/individual)

**Estadísticas mostradas**:
- Porcentaje de asistencia general
- Total de presentes, ausentes, tardanzas, justificados
- Comparativas por periodo
- Tendencias (opcional)

**Acciones**:
- Exportar PDF (pendiente)
- Exportar Excel (pendiente)
- Compartir (pendiente)
- Generar gráficos (opcional con MPAndroidChart)

### Componentes Reutilizables

#### GrupoCard
**Archivo**: `ui/components/GrupoCard.kt`

**Propósito**: Mostrar información de un grupo en formato card

**Props**:
- `grupo: Grupo`: Datos del grupo
- `onCardClick: (Long) -> Unit`: Al hacer clic en el card
- `onAttendanceClick: (Long) -> Unit`: Botón de asistencia
- `onEditClick: (Long) -> Unit`: Botón de edición

**Información mostrada**:
- Nombre del grupo
- Materia
- Número de estudiantes (si disponible)
- Última fecha de asistencia

**Diseño**:
- Card con elevación
- Layout organizado con Row/Column
- Iconos Material para acciones
- Colores del tema Material 3

#### EstudianteCard
**Archivo**: `ui/components/EstudianteCard.kt`

**Propósito**: Mostrar información de un estudiante

**Props**:
- `estudiante: Estudiante`: Datos del estudiante
- `onClick: () -> Unit`: Acción al hacer clic
- `onEditClick: () -> Unit`: Editar estudiante
- `onDeleteClick: () -> Unit`: Eliminar estudiante

**Información mostrada**:
- Foto de perfil (placeholder si no tiene)
- Nombre completo
- Código de estudiante
- Email (si tiene)

**Diseño**:
- Card con imagen circular
- Acciones con IconButtons
- Swipe actions (opcional)

#### EstudianteAttendanceCard
**Archivo**: `ui/components/EstudianteAttendanceCard.kt`

**Propósito**: Card especializado para toma de asistencia

**Props**:
- `estudiante: Estudiante`: Datos del estudiante
- `estadoActual: EstadoAsistencia?`: Estado actual de asistencia
- `onEstadoChange: (EstadoAsistencia) -> Unit`: Callback al cambiar estado

**Información mostrada**:
- Foto y nombre del estudiante
- 4 botones de estado (Presente, Ausente, Tardanza, Justificado)
- Indicador visual del estado seleccionado

**Diseño**:
- Card compacto optimizado para scroll
- Botones grandes y fáciles de presionar
- Código de colores según estado
- Animación al seleccionar
- Feedback háptico (opcional)

### Tema y Estilos (Material 3)

**Color.kt**: Define la paleta de colores

```kotlin
// Colores principales (modo claro)
val PrimaryColor = Color(0xFF6200EE)
val SecondaryColor = Color(0xFF03DAC6)
val TertiaryColor = Color(0xFFFF9800)
val ErrorColor = Color(0xFFB00020)

// Colores de fondo
val BackgroundColor = Color(0xFFFFFBFE)
val SurfaceColor = Color(0xFFFFFBFE)

// Modo oscuro
val PrimaryColorDark = Color(0xFFBB86FC)
val SecondaryColorDark = Color(0xFF03DAC6)
// ...
```

**Theme.kt**: Configura el tema Material 3

```kotlin
@Composable
fun AsisteDocenteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

**Type.kt**: Tipografía

```kotlin
val Typography = Typography(
    displayLarge = TextStyle(...),
    titleLarge = TextStyle(...),
    bodyMedium = TextStyle(...),
    // ...
)
```

---

## 🔌 Inyección de Dependencias (Hilt)

### Módulos Configurados

#### DatabaseModule
**Archivo**: `di/DatabaseModule.kt`

**Provee**:
- `AppDatabase`: Instancia singleton de la base de datos
- `DocenteDao`: DAO de docentes
- `GrupoDao`: DAO de grupos
- `EstudianteDao`: DAO de estudiantes
- `RegistroAsistenciaDao`: DAO de registros

**Anotaciones**:
- `@Module`: Marca como módulo de Hilt
- `@InstallIn(SingletonComponent::class)`: Scope de aplicación
- `@Provides`: Métodos que proveen dependencias
- `@Singleton`: Una sola instancia en toda la app

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "asiste_docente_db"
        )
        .fallbackToDestructiveMigration() // Solo en desarrollo
        .build()
    }

    @Provides
    fun provideDocenteDao(db: AppDatabase): DocenteDao = db.docenteDao()

    // ... otros DAOs
}
```

#### RepositoryModule
**Archivo**: `di/RepositoryModule.kt`

**Provee**:
- `DocenteRepository`: Repository de docentes
- `GrupoRepository`: Repository de grupos
- `EstudianteRepository`: Repository de estudiantes
- `AsistenciaRepository`: Repository de asistencia

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDocenteRepository(
        docenteDao: DocenteDao
    ): DocenteRepository {
        return DocenteRepository(docenteDao)
    }

    // ... otros repositories
}
```

### Uso en ViewModels

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val grupoRepository: GrupoRepository
) : ViewModel() {
    // ViewModel puede usar el repository directamente
}
```

### Uso en Activities

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    // Habilita inyección de dependencias
}
```

### MainApplication

```kotlin
@HiltAndroidApp
class MainApplication : Application() {
    // Inicializa Hilt
}
```

---

## 🛠️ Utilidades

### Constants.kt
**Archivo**: `utils/Constants.kt`

Constantes globales de la aplicación:

```kotlin
object Constants {
    // Formatos de fecha
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_FILE = "yyyyMMdd_HHmmss"
    const val TIME_FORMAT = "HH:mm"

    // Límites
    const val MAX_NOMBRE_LENGTH = 100
    const val MAX_CODIGO_LENGTH = 20
    const val MIN_NOMBRE_LENGTH = 2

    // Base de datos
    const val DATABASE_NAME = "asiste_docente_db"
    const val DATABASE_VERSION = 1

    // Archivos
    const val PDF_DIRECTORY = "reportes_pdf"
    const val EXCEL_DIRECTORY = "reportes_excel"
    const val PHOTOS_DIRECTORY = "fotos_estudiantes"

    // Tipos MIME
    const val MIME_TYPE_PDF = "application/pdf"
    const val MIME_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    // Preferencias
    const val PREFS_NAME = "asiste_docente_prefs"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_LAST_SYNC = "last_sync"
}
```

### DateUtils.kt
**Archivo**: `utils/DateUtils.kt`

Utilidades para manejo de fechas y timestamps:

**Funciones principales**:

```kotlin
object DateUtils {
    // Obtener timestamp normalizado (00:00:00 del día)
    fun getStartOfDay(timestamp: Long): Long

    // Timestamp de hoy a las 00:00:00
    fun getTodayStart(): Long

    // Formatear timestamp a string legible
    fun formatDate(timestamp: Long, pattern: String = DATE_FORMAT_DISPLAY): String

    // Parse string a timestamp
    fun parseDate(dateString: String, pattern: String = DATE_FORMAT_DISPLAY): Long?

    // Verificar si dos timestamps son el mismo día
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean

    // Obtener primer día del mes
    fun getFirstDayOfMonth(timestamp: Long): Long

    // Obtener último día del mes
    fun getLastDayOfMonth(timestamp: Long): Long

    // Calcular diferencia en días
    fun daysBetween(start: Long, end: Long): Int

    // Obtener día de la semana
    fun getDayOfWeek(timestamp: Long): String

    // Formatear rango de fechas
    fun formatDateRange(start: Long, end: Long): String
}
```

**Uso en ViewModel**:
```kotlin
val hoy = DateUtils.getTodayStart()
val formatted = DateUtils.formatDate(System.currentTimeMillis())
```

### PdfGenerator.kt (TODO)
**Archivo**: `utils/PdfGenerator.kt`

**Funcionalidad pendiente**:
- Generar reportes PDF con iText7
- Incluir tabla de asistencia
- Agregar estadísticas
- Logo y encabezados
- Formato profesional

**Métodos esperados**:
```kotlin
class PdfGenerator {
    fun generateAttendanceReport(
        grupo: Grupo,
        registros: List<RegistroAsistencia>,
        fechaInicio: Long,
        fechaFin: Long,
        outputFile: File
    ): Result<File>

    fun generateStudentReport(
        estudiante: Estudiante,
        registros: List<RegistroAsistencia>,
        outputFile: File
    ): Result<File>
}
```

### ExcelGenerator.kt (TODO)
**Archivo**: `utils/ExcelGenerator.kt`

**Funcionalidad pendiente**:
- Generar archivos Excel con Apache POI
- Formato tabular de asistencia
- Múltiples hojas si necesario
- Estilos y formato

**Métodos esperados**:
```kotlin
class ExcelGenerator {
    fun generateAttendanceSpreadsheet(
        grupo: Grupo,
        estudiantes: List<Estudiante>,
        registros: List<RegistroAsistencia>,
        fechaInicio: Long,
        fechaFin: Long,
        outputFile: File
    ): Result<File>
}
```

### PermissionUtils.kt (TODO)
**Archivo**: `utils/PermissionUtils.kt`

**Funcionalidad pendiente**:
- Helper para solicitar permisos en runtime
- Manejo de diferentes versiones de Android
- Permisos: CAMERA, READ_MEDIA_IMAGES, WRITE_EXTERNAL_STORAGE

**Métodos esperados**:
```kotlin
class PermissionUtils {
    fun checkCameraPermission(context: Context): Boolean
    fun requestCameraPermission(activity: ComponentActivity)
    fun checkStoragePermission(context: Context): Boolean
    fun requestStoragePermission(activity: ComponentActivity)
}
```

---

## 📱 AndroidManifest.xml

### Permisos Declarados

```xml
<!-- Almacenamiento (API ≤32) -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />

<!-- Android 13+ (API 33+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />

<!-- Cámara para fotos de estudiantes -->
<uses-permission android:name="android.permission.CAMERA" />

<!-- Internet para futuras sincronizaciones -->
<uses-permission android:name="android.permission.INTERNET" />
```

### FileProvider

Configurado para compartir archivos PDF/Excel:

```xml
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
```

**file_paths.xml**:
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path name="pdf_files" path="reportes_pdf/"/>
    <external-files-path name="excel_files" path="reportes_excel/"/>
    <external-files-path name="photos" path="fotos_estudiantes/"/>
</paths>
```

---

## 📂 Estructura de Archivos del Proyecto

```
AsisteDocente/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/sv/edu/catolica/asistedocente/
│   │   │   │   ├── data/
│   │   │   │   │   ├── local/
│   │   │   │   │   │   ├── dao/
│   │   │   │   │   │   │   ├── DocenteDao.kt
│   │   │   │   │   │   │   ├── GrupoDao.kt
│   │   │   │   │   │   │   ├── EstudianteDao.kt
│   │   │   │   │   │   │   └── RegistroAsistenciaDao.kt
│   │   │   │   │   │   ├── entities/
│   │   │   │   │   │   │   ├── Docente.kt
│   │   │   │   │   │   │   ├── Grupo.kt
│   │   │   │   │   │   │   ├── Estudiante.kt
│   │   │   │   │   │   │   ├── RegistroAsistencia.kt
│   │   │   │   │   │   │   └── EstadoAsistencia.kt
│   │   │   │   │   │   └── database/
│   │   │   │   │   │       ├── AppDatabase.kt
│   │   │   │   │   │       └── Converters.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       ├── DocenteRepository.kt
│   │   │   │   │       ├── GrupoRepository.kt
│   │   │   │   │       ├── EstudianteRepository.kt
│   │   │   │   │       └── AsistenciaRepository.kt
│   │   │   │   ├── di/
│   │   │   │   │   ├── DatabaseModule.kt
│   │   │   │   │   └── RepositoryModule.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── home/
│   │   │   │   │   │   │   ├── HomeScreen.kt
│   │   │   │   │   │   │   └── HomeViewModel.kt
│   │   │   │   │   │   ├── groups/
│   │   │   │   │   │   │   ├── GroupDetailScreen.kt
│   │   │   │   │   │   │   ├── GroupDetailViewModel.kt
│   │   │   │   │   │   │   ├── AddEditGroupScreen.kt
│   │   │   │   │   │   │   └── AddEditGroupViewModel.kt
│   │   │   │   │   │   ├── students/
│   │   │   │   │   │   │   ├── AddEditStudentScreen.kt
│   │   │   │   │   │   │   └── AddEditStudentViewModel.kt
│   │   │   │   │   │   ├── attendance/
│   │   │   │   │   │   │   ├── AttendanceScreen.kt
│   │   │   │   │   │   │   └── AttendanceViewModel.kt
│   │   │   │   │   │   └── reports/
│   │   │   │   │   │       ├── ReportsScreen.kt
│   │   │   │   │   │       └── ReportsViewModel.kt
│   │   │   │   │   ├── components/
│   │   │   │   │   │   ├── GrupoCard.kt
│   │   │   │   │   │   ├── EstudianteCard.kt
│   │   │   │   │   │   └── EstudianteAttendanceCard.kt
│   │   │   │   │   ├── navigation/
│   │   │   │   │   │   ├── Screen.kt
│   │   │   │   │   │   └── NavGraph.kt
│   │   │   │   │   └── theme/
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   ├── utils/
│   │   │   │   │   ├── Constants.kt
│   │   │   │   │   ├── DateUtils.kt
│   │   │   │   │   ├── PdfGenerator.kt (TODO)
│   │   │   │   │   ├── ExcelGenerator.kt (TODO)
│   │   │   │   │   └── PermissionUtils.kt (TODO)
│   │   │   │   ├── MainApplication.kt
│   │   │   │   └── MainActivity.kt
│   │   │   ├── res/
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── xml/
│   │   │   │       ├── file_paths.xml
│   │   │   │       ├── backup_rules.xml
│   │   │   │       └── data_extraction_rules.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/ (Unit tests)
│   │   └── androidTest/ (Instrumentation tests)
│   └── build.gradle (Groovy DSL)
├── build.gradle (Project level - Groovy DSL)
├── gradle.properties
├── settings.gradle (Groovy DSL)
├── CLAUDE.md
├── DOCUMENTACION.md (este archivo)
├── SETUP_COMPLETED.md
└── VERIFICACION_BUILD.md
```

---

## 🔄 Flujos de Usuario Principales

### Flujo 1: Crear Grupo y Agregar Estudiantes

1. Usuario abre la app → **HomeScreen**
2. Click en FAB "+" → Navega a **AddEditGroupScreen**
3. Completa formulario de grupo (nombre, materia, horario)
4. Guarda grupo → Regresa a **HomeScreen** (nuevo grupo visible)
5. Click en card del grupo → Navega a **GroupDetailScreen**
6. Click en FAB "Agregar Estudiante" → Navega a **AddEditStudentScreen**
7. Completa datos del estudiante (nombre, apellido, código)
8. Guarda estudiante → Regresa a **GroupDetailScreen** (estudiante visible)
9. Repite pasos 6-8 para más estudiantes

**Componentes involucrados**:
- HomeScreen, HomeViewModel
- AddEditGroupScreen, AddEditGroupViewModel
- GroupDetailScreen, GroupDetailViewModel
- AddEditStudentScreen, AddEditStudentViewModel
- GrupoRepository, EstudianteRepository
- GrupoDao, EstudianteDao

### Flujo 2: Tomar Asistencia (CRÍTICO)

1. Usuario en **HomeScreen** o **GroupDetailScreen**
2. Click en botón "Tomar Asistencia" → Navega a **AttendanceScreen**
3. **AttendanceScreen** carga:
   - Lista de estudiantes del grupo
   - Fecha actual (editable)
   - Registros existentes para esa fecha
4. Por cada estudiante:
   - Muestra **EstudianteAttendanceCard**
   - 4 botones: Presente, Ausente, Tardanza, Justificado
   - Estado actual resaltado (si existe)
5. Docente toca botón de estado para cada estudiante
6. ViewModel guarda automáticamente cada cambio
7. Feedback visual: botón seleccionado, color cambia
8. Al terminar, botón "Guardar" o navegación atrás
9. Confirmación y regreso a pantalla anterior

**Performance**:
- Carga inicial: <1 segundo
- Guardado: debouncing para evitar múltiples writes
- UI: scroll suave, botones grandes

**Validaciones**:
- Constraint único previene duplicados (estudianteId, fecha)
- Si ya existe registro, se actualiza en lugar de insertar
- Feedback de error si algo falla

**Componentes involucrados**:
- AttendanceScreen, AttendanceViewModel
- EstudianteAttendanceCard
- AsistenciaRepository
- RegistroAsistenciaDao
- DateUtils

### Flujo 3: Ver Reportes y Estadísticas

1. Usuario en **HomeScreen**
2. Click en icono de reportes → Navega a **ReportsScreen**
3. **ReportsScreen** muestra:
   - Selector de grupo (dropdown)
   - Selector de rango de fechas (date pickers)
   - Vista previa de estadísticas
4. Usuario selecciona grupo y fechas
5. ViewModel carga datos y calcula estadísticas:
   - Total de registros
   - Porcentaje de asistencia
   - Count por estado (presente, ausente, etc.)
6. Muestra resultados en pantalla
7. Usuario puede exportar:
   - Click en "Exportar PDF" → genera PDF (TODO)
   - Click en "Exportar Excel" → genera Excel (TODO)
8. Sistema de compartir archivos (TODO)

**Componentes involucrados**:
- ReportsScreen, ReportsViewModel
- AsistenciaRepository
- PdfGenerator (TODO)
- ExcelGenerator (TODO)

### Flujo 4: Editar Estudiante

1. Usuario en **GroupDetailScreen**
2. Click en card de estudiante → acción editar
3. Navega a **AddEditStudentScreen** con ID del estudiante
4. Screen carga datos existentes del estudiante
5. Usuario modifica campos deseados
6. Guarda cambios
7. ViewModel valida y actualiza en BD
8. Regresa a **GroupDetailScreen** con cambios reflejados

**Validaciones**:
- Código único (si se cambia)
- Campos requeridos completos
- Email con formato válido

---

## ✅ Funcionalidades Implementadas

### ✅ Completamente Implementado

1. **Base de Datos Room**
   - ✅ 4 entidades (Docente, Grupo, Estudiante, RegistroAsistencia)
   - ✅ 4 DAOs con queries optimizadas
   - ✅ Foreign keys y constraints
   - ✅ Índices para performance
   - ✅ Type converters para enums
   - ✅ AppDatabase configurada

2. **Repositories**
   - ✅ DocenteRepository
   - ✅ GrupoRepository
   - ✅ EstudianteRepository
   - ✅ AsistenciaRepository
   - ✅ Lógica de negocio y validaciones

3. **Inyección de Dependencias (Hilt)**
   - ✅ DatabaseModule
   - ✅ RepositoryModule
   - ✅ MainApplication con @HiltAndroidApp
   - ✅ ViewModels con @HiltViewModel

4. **Pantallas UI (Jetpack Compose)**
   - ✅ HomeScreen con lista de grupos
   - ✅ GroupDetailScreen con estudiantes
   - ✅ AddEditGroupScreen (formulario CRUD)
   - ✅ AddEditStudentScreen (formulario CRUD)
   - ✅ AttendanceScreen (toma de asistencia) ⭐
   - ✅ ReportsScreen (visualización)

5. **ViewModels**
   - ✅ HomeViewModel
   - ✅ GroupDetailViewModel
   - ✅ AddEditGroupViewModel
   - ✅ AddEditStudentViewModel
   - ✅ AttendanceViewModel
   - ✅ ReportsViewModel

6. **Navegación**
   - ✅ Screen.kt con sealed class de rutas
   - ✅ NavGraph.kt configurado
   - ✅ Navegación entre todas las pantallas
   - ✅ Paso de argumentos (IDs, fechas)

7. **Componentes Reutilizables**
   - ✅ GrupoCard
   - ✅ EstudianteCard
   - ✅ EstudianteAttendanceCard

8. **Tema Material 3**
   - ✅ Color.kt con paletas claro/oscuro
   - ✅ Theme.kt con soporte dynamic color
   - ✅ Type.kt con tipografía
   - ✅ Modo claro y oscuro funcional

9. **Utilidades**
   - ✅ Constants.kt con constantes globales
   - ✅ DateUtils.kt con funciones de fecha

10. **Configuración**
    - ✅ Permisos en AndroidManifest
    - ✅ FileProvider configurado
    - ✅ Groovy DSL en build.gradle
    - ✅ Dependencias correctas

### 🔨 Parcialmente Implementado

1. **Gestión de Fotos**
   - ✅ Campo foto en entity Estudiante
   - ✅ Coil dependency instalada
   - ❌ Captura/selección de foto
   - ❌ Display de foto en cards
   - ❌ Manejo de permisos Camera

2. **Reportes**
   - ✅ ReportsScreen con filtros
   - ✅ Cálculo de estadísticas
   - ✅ iText7 y Apache POI instalados
   - ❌ PdfGenerator implementado
   - ❌ ExcelGenerator implementado
   - ❌ Sistema de compartir archivos

### ❌ Pendiente de Implementar

1. **PdfGenerator.kt**
   - Generar reportes PDF profesionales
   - Usar iText7
   - Incluir tablas y gráficos
   - Logo y encabezados

2. **ExcelGenerator.kt**
   - Generar archivos .xlsx
   - Usar Apache POI
   - Formato tabular
   - Múltiples hojas

3. **PermissionUtils.kt**
   - Helper para permisos en runtime
   - Manejar Camera y Storage
   - Compatibilidad Android 8-14

4. **Sistema de Compartir**
   - Intent chooser
   - Compartir PDF/Excel
   - WhatsApp, Email, Drive

5. **Captura de Fotos**
   - Tomar foto con cámara
   - Seleccionar de galería
   - Guardar en storage
   - Mostrar en UI

6. **Funcionalidades Avanzadas** (baja prioridad)
   - Backup/Restore de BD
   - Notificaciones recordatorias
   - Widget de home screen
   - Sincronización en nube
   - Gráficos con MPAndroidChart

---

## 🎯 Prioridades de Desarrollo

### Alta Prioridad ⭐⭐⭐

1. **Implementar PdfGenerator**
   - Necesario para funcionalidad core de reportes
   - Docentes necesitan reportes profesionales
   - iText7 ya está instalado

2. **Implementar ExcelGenerator**
   - Complemento necesario para reportes
   - Formato Excel muy solicitado
   - Apache POI ya está instalado

3. **Sistema de Compartir Archivos**
   - Permite distribuir reportes generados
   - Usa FileProvider ya configurado
   - Implementación relativamente simple

### Media Prioridad ⭐⭐

4. **PermissionUtils y Captura de Fotos**
   - Mejora UX de gestión de estudiantes
   - Permisos ya declarados en Manifest
   - Coil ya instalado para display

5. **Optimización de AttendanceScreen**
   - Ya funcional pero puede mejorarse
   - Performance crítica para UX
   - Agregar indicadores de guardado

### Baja Prioridad ⭐

6. **Gráficos en Reportes**
   - MPAndroidChart ya instalado
   - Nice-to-have para visualización

7. **Backup/Restore**
   - Útil para migración de dispositivos
   - No crítico mientras haya solo offline

8. **Widget y Notificaciones**
   - Mejoras de UX
   - No afecta funcionalidad core

---

## 🧪 Testing

### Unit Tests
**Ubicación**: `app/src/test/`

**Tests pendientes**:
- ViewModels: Mock repositories, verificar state changes
- Repositories: Mock DAOs, verificar lógica de negocio
- DateUtils: Verificar cálculos de fechas
- Validaciones: Verificar reglas de negocio

### Integration Tests
**Ubicación**: `app/src/androidTest/`

**Tests pendientes**:
- DAOs: Verificar queries en BD real
- Database migrations: Si se cambia el schema
- Flow de navegación: UI tests end-to-end

### Testing con Hilt
- Usar `@HiltAndroidTest` para tests instrumentados
- `hiltRule` para inicializar Hilt en tests

---

## 🐛 Troubleshooting

### Errores Comunes

#### 1. KAPT errors con Room/Hilt
**Síntoma**: Clases generadas no se encuentran

**Solución**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

#### 2. Foreign Key Constraint Failed
**Síntoma**: Crash al insertar/eliminar registros

**Causa**: Violación de integridad referencial

**Solución**: Verificar que las FKs existen antes de insertar

#### 3. Duplicate Entry for Unique Index
**Síntoma**: Error al insertar estudiante con código existente

**Solución**: Validar código único antes de insertar

#### 4. Compose Navigation Argument Error
**Síntoma**: IllegalStateException al obtener argumentos

**Solución**: Verificar que el argumento está definido en el route y extraído correctamente

#### 5. Flow Not Collecting
**Síntoma**: UI no se actualiza automáticamente

**Solución**: Usar `collectAsState()` en Composables

---

## 📊 Estadísticas del Proyecto

### Código

- **Archivos Kotlin**: ~42
- **Líneas de código**: ~2,500+
- **Entidades**: 4
- **DAOs**: 4
- **Repositories**: 4
- **ViewModels**: 6
- **Screens**: 6
- **Componentes reutilizables**: 3

### Dependencias

- **Total de dependencias**: ~50
- **Tamaño de descarga**: ~500MB (primera vez)
- **APK Debug size**: ~15-20MB (sin optimización)

### Completitud

- **Fase 1 (Foundation)**: 100% ✅
- **Fase 2 (UI)**: 100% ✅
- **Fase 3 (Reports)**: 30% 🔨
- **Fase 4 (Polish)**: 0% ❌

**Estimado general**: ~75% completo

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos

1. **Android Studio**: Electric Eel o superior
2. **JDK**: 17
3. **Android SDK**: API 34
4. **Gradle**: 8.2+ (wrapper incluido)
5. **Dispositivo/Emulador**: Android 8.0+ (API 26+)

### Pasos

1. **Clonar el repositorio** (o abrir proyecto existente)
2. **Abrir en Android Studio**
3. **Sync Gradle**: File → Sync Project with Gradle Files
4. **Esperar descarga de dependencias** (primera vez: 5-10 minutos)
5. **Ejecutar**: Run → Run 'app' (Shift+F10)

### Comandos de Terminal

```bash
# Build
./gradlew build

# Clean build
./gradlew clean build

# Install debug APK
./gradlew installDebug

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

---

## 📝 Notas Importantes

### Offline-First
- La app funciona **100% offline**
- No requiere conexión a internet
- Toda la data se almacena localmente en Room

### Privacidad
- Datos sensibles de estudiantes
- No se envía información a servidores externos
- Cumplir normativas de protección de datos

### Performance
- **Objetivo**: Pantalla de asistencia carga en <1 segundo
- Usar LazyColumn para listas largas
- Debouncing en búsquedas
- Índices en BD para queries frecuentes

### Código Limpio
- Comentarios en español
- Naming conventions consistentes
- Principios SOLID
- DRY (Don't Repeat Yourself)

---

## 🔗 Enlaces Útiles

### Documentación Oficial
- [Android Developers](https://developer.android.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- [Material Design 3](https://m3.material.io/)

### Librerías
- [iText7](https://github.com/itext/itext7)
- [Apache POI](https://poi.apache.org/)
- [Coil](https://coil-kt.github.io/coil/)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)

### Referencias del Proyecto
- **Video demo**: https://streamable.com/hya7nq
- **Prototipo**: https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit

---

## 👥 Autores y Mantenimiento

**Proyecto**: AsisteDocente
**Universidad**: Universidad Católica de El Salvador
**Package**: sv.edu.catolica.asistedocente

Para preguntas o contribuciones, consultar CLAUDE.md para guías de desarrollo.

---

## 📜 Licencia

Este proyecto es para uso educativo en la Universidad Católica de El Salvador.

---

**Última actualización**: 2025-11-10
**Versión del documento**: 1.0
