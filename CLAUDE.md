# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**AsisteDocente** (AsistenciaDocente / AttendanceTeacher) es una aplicación móvil nativa para Android que gestiona la asistencia de estudiantes. Permite a los docentes registrar y controlar asistencia desde sus dispositivos móviles de forma rápida durante la clase, consultar historial de asistencia y generar reportes en PDF/Excel.

**Package name**: `sv.edu.catolica.asistedocente`

**🎥 REFERENCIA VISUAL PRINCIPAL**: https://streamable.com/hya7nq
**IMPORTANTE**: Este video muestra el funcionamiento completo y diseño exacto de la aplicación. Es la referencia PRINCIPAL para cualquier decisión de diseño, colores, flujo de navegación, e interacciones.

**Prototipo de diseño (secundario)**: https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit

**Nota sobre referencias**: Si hay discrepancias entre el video y el prototipo de Canva, el **VIDEO tiene prioridad absoluta** ya que representa la funcionalidad y diseño real esperado.

## Build System

Este proyecto usa **Gradle con Groovy DSL** (NO Kotlin DSL). Todos los archivos de configuración de Gradle usan sintaxis `.gradle` (NO `.gradle.kts`).

### Common Commands

**IMPORTANTE para Windows**: Este proyecto está en Windows. Usar:
- `./gradlew <task>` en Git Bash/MINGW64 (recomendado para Claude Code)
- `gradlew.bat <task>` en Command Prompt/PowerShell (ejemplo: `gradlew.bat build`)
- O usar Android Studio (recomendado para primera compilación)

**Nota sobre primera ejecución**: La primera vez que ejecutes Gradle se iniciará el Gradle Daemon, lo que añade ~10-30 segundos. Las siguientes ejecuciones serán mucho más rápidas.

**Build the project**:
```bash
./gradlew build
# Primera compilación puede tomar 10-15 minutos (descarga dependencias ~500MB)
# También iniciará Gradle Daemon si no está corriendo
```

**Clean build**:
```bash
./gradlew clean build
```

**Assemble debug APK** (más rápido que build completo):
```bash
./gradlew assembleDebug
# APK generado en: app/build/outputs/apk/debug/app-debug.apk
```

**Install debug APK to connected device**:
```bash
./gradlew installDebug
# O desde Android Studio: Run → Run 'app'
```

**Run unit tests**:
```bash
./gradlew test
./gradlew test --tests sv.edu.catolica.asistedocente.ExampleUnitTest  # Test específico
```

**Run instrumented (Android) tests** (requiere dispositivo/emulador):
```bash
./gradlew connectedAndroidTest
./gradlew connectedDebugAndroidTest  # Solo debug variant
```

**Run lint checks** (análisis de código estático):
```bash
./gradlew lint           # Genera reporte HTML
./gradlew lintDebug      # Solo debug variant
./gradlew lintFix        # Aplica correcciones automáticas
```

**Check dependencies** (útil para debug):
```bash
./gradlew dependencies
./gradlew app:dependencies  # Solo del módulo app
```

**Generate Room database schema**:
```bash
./gradlew assembleDebug
# Los esquemas JSON se exportan en: app/schemas/sv.edu.catolica.asistedocente.data.local.database.AppDatabase/
# Versión actual: 1.json
```

**Inspect database schema**:
```bash
cat app/schemas/sv.edu.catolica.asistedocente.data.local.database.AppDatabase/1.json
# Ver estructura completa de la BD exportada por Room
```

**Clean gradle cache** (si hay problemas):
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

**List all available tasks**:
```bash
./gradlew tasks
./gradlew tasks --all  # Incluir tasks ocultas
./gradlew tasks --group=build  # Solo tareas de build
./gradlew tasks --group=verification  # Solo tareas de testing/lint
```

**Quick project verification** (verificar que todo compila):
```bash
./gradlew assembleDebug testDebugUnitTest lintDebug
# Compila APK, corre tests unitarios y lint en una sola línea
```

**Sync with Android Studio** (recomendado como primer paso):
1. Abrir proyecto en Android Studio
2. File → Sync Project with Gradle Files
3. Esperar a que descargue dependencias
4. Verificar que no hay errores en Build tab

## Architecture

### Technology Stack

- **Language**: Kotlin (version 1.9.22)
- **UI Framework**: Jetpack Compose with Material 3
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite) - offline-first
- **Dependency Injection**: Hilt (Dagger)
- **Async**: Kotlin Coroutines + Flow
- **Build Tool**: Gradle con Groovy DSL
- **Minimum SDK**: 26 (Android 8.0 Oreo) - Requerido por Apache POI/Log4j
- **Target SDK**: 34 (Android 14)
- **JVM Target**: Java 17

### Key Dependencies

**Core:**
- Room Database: 2.6.1
- Hilt: 2.50
- Jetpack Compose: 1.6.0
- Material 3
- Kotlin Coroutines: 1.7.3

**Reports:**
- iText7 (PDF generation): 7.2.5
- Apache POI (Excel generation): 5.2.3

**Images:**
- Coil (for Compose): 2.5.0

**Charts:**
- MPAndroidChart: v3.1.0

**Background & Storage:**
- WorkManager: 2.9.0 (tareas en background)
- DataStore Preferences: 1.0.0 (preferencias app)

### Database Schema

La aplicación usa Room Database con las siguientes entidades principales:

**Versión actual**: 1 (definida en `AppDatabase.kt`)
**Database name**: `asiste_docente_db`
**Esquema exportado**: `app/schemas/sv.edu.catolica.asistedocente.data.local.database.AppDatabase/1.json`

#### Entities

1. **Docente** (`docentes`)
   - id (PK, autoincrement)
   - nombre, apellido, email, telefono
   - foto (URI), activo, fechaCreacion

2. **Grupo** (`grupos`)
   - id (PK, autoincrement)
   - nombre, materia, horario, descripcion
   - docenteId (FK → Docente)
   - activo, fechaCreacion

3. **Estudiante** (`estudiantes`)
   - id (PK, autoincrement)
   - nombre, apellido, codigo (único)
   - email, foto (URI)
   - grupoId (FK → Grupo)
   - activo, fechaCreacion

4. **RegistroAsistencia** (`registros_asistencia`)
   - id (PK, autoincrement)
   - estudianteId (FK → Estudiante)
   - grupoId (FK → Grupo)
   - fecha (timestamp), estado (enum)
   - notas, horaRegistro
   - Constraint: UNIQUE(estudianteId, fecha) - evita duplicados

#### Estados de Asistencia

```kotlin
enum class EstadoAsistencia {
    PRESENTE,
    AUSENTE,
    TARDANZA,
    JUSTIFICADO
}
```

### Project Structure

```
app/src/main/java/sv/edu/catolica/asistedocente/
├── data/
│   ├── local/
│   │   ├── dao/                      # Data Access Objects
│   │   │   ├── DocenteDao.kt
│   │   │   ├── GrupoDao.kt
│   │   │   ├── EstudianteDao.kt
│   │   │   └── RegistroAsistenciaDao.kt
│   │   ├── entities/                 # Room entities
│   │   │   ├── Docente.kt
│   │   │   ├── Grupo.kt
│   │   │   ├── Estudiante.kt
│   │   │   ├── RegistroAsistencia.kt
│   │   │   └── EstadoAsistencia.kt
│   │   └── database/
│   │       ├── AppDatabase.kt        # Room database
│   │       └── Converters.kt         # Type converters
│   └── repository/                   # Repository pattern
│       ├── DocenteRepository.kt
│       ├── GrupoRepository.kt
│       ├── EstudianteRepository.kt
│       └── AsistenciaRepository.kt
├── di/                               # Hilt dependency injection
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── ui/
│   ├── screens/                      # Pantallas de la app
│   │   ├── login/                    # ✅ Authentication
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   ├── register/                 # ✅ User registration
│   │   │   ├── RegisterScreen.kt
│   │   │   └── RegisterViewModel.kt
│   │   ├── profile/                  # ✅ Docente profile
│   │   │   ├── DocenteProfileScreen.kt
│   │   │   └── DocenteProfileViewModel.kt
│   │   ├── home/                     # ✅ Dashboard
│   │   │   ├── HomeScreen.kt        # UI del dashboard
│   │   │   └── HomeViewModel.kt     # Lógica del dashboard
│   │   ├── groups/                   # ✅ Gestión de grupos
│   │   │   ├── GroupDetailScreen.kt # Detalle y estudiantes de grupo
│   │   │   ├── GroupDetailViewModel.kt
│   │   │   ├── AddEditGroupScreen.kt # CRUD de grupos
│   │   │   └── AddEditGroupViewModel.kt
│   │   ├── students/                 # ✅ Gestión de estudiantes
│   │   │   ├── AddEditStudentScreen.kt # CRUD de estudiantes
│   │   │   └── AddEditStudentViewModel.kt
│   │   ├── attendance/               # ✅ Toma de asistencia ⭐
│   │   │   ├── AttendanceScreen.kt  # Pantalla crítica de asistencia
│   │   │   └── AttendanceViewModel.kt
│   │   ├── reports/                  # ✅ Reportes y exportación
│   │   │   ├── ReportsScreen.kt     # Visualización de reportes
│   │   │   └── ReportsViewModel.kt
│   │   └── settings/                 # ✅ App settings
│   │       ├── SettingsScreen.kt
│   │       └── SettingsViewModel.kt
│   ├── components/                   # Componentes reutilizables ✅
│   │   ├── GrupoCard.kt             # Card para mostrar grupos
│   │   ├── EstudianteCard.kt        # Card para mostrar estudiantes
│   │   └── EstudianteAttendanceCard.kt # Card para toma de asistencia
│   ├── navigation/                   # Navegación ✅
│   │   ├── Screen.kt                # Definición de rutas
│   │   └── NavGraph.kt              # Grafo de navegación
│   └── theme/                        # Tema Material 3
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
├── utils/                            # Utilidades ✅
│   ├── Constants.kt                 # ✅ Constantes globales
│   ├── DateUtils.kt                 # ✅ Utilidades de fecha
│   ├── PdfGenerator.kt              # ✅ Generar PDFs (iText7)
│   ├── ExcelGenerator.kt            # ✅ Generar Excel (Apache POI)
│   ├── ImageHandler.kt              # ✅ Manejo de imágenes y fotos
│   ├── ValidationUtils.kt           # ✅ Validaciones de formularios
│   ├── AuthHelper.kt                # ✅ Helpers de autenticación
│   ├── LocaleHelper.kt              # ✅ Internacionalización
│   └── SampleDataGenerator.kt       # ✅ Generación de datos de prueba
├── MainApplication.kt                # Application class con @HiltAndroidApp
└── MainActivity.kt                   # Activity principal con @AndroidEntryPoint
```

### MVVM Architecture Flow

```
UI (Composables) ←→ ViewModel ←→ Repository ←→ DAO ←→ Room Database
```

- **UI Layer**: Composables (Jetpack Compose)
- **ViewModel**: Maneja estado UI y lógica de presentación
- **Repository**: Single source of truth, abstrae fuentes de datos
- **DAO**: Queries SQL type-safe
- **Database**: Room SQLite local

### Dependency Injection with Hilt

**Modules configurados:**
- `DatabaseModule`: Provee AppDatabase y todos los DAOs
- `RepositoryModule`: Provee todos los Repositories

**Usage en ViewModels:**
```kotlin
@HiltViewModel
class MyViewModel @Inject constructor(
    private val grupoRepository: GrupoRepository
) : ViewModel() { ... }
```

**Usage en Composables:**
```kotlin
@Composable
fun MyScreen(
    viewModel: MyViewModel = hiltViewModel()
) { ... }
```

**Usage en Activities:**
```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

### Navigation System

El proyecto usa **Navigation Compose** con un sistema de rutas centralizado:

**Screen.kt** - Define todas las rutas como sealed class:
```kotlin
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object GroupDetail : Screen("group/{groupId}") {
        fun createRoute(groupId: Long) = "group/$groupId"
    }
    // ... más rutas
}
```

**NavGraph.kt** - Configura el grafo de navegación con:
- Rutas y argumentos (groupId, studentId, date, etc.)
- Lambdas de navegación pasadas a cada screen
- Integración con ViewModels via hiltViewModel()

**Patrón de navegación:**
```kotlin
// En HomeScreen
HomeScreen(
    onNavigateToGroupDetail = { groupId ->
        navController.navigate(Screen.GroupDetail.createRoute(groupId))
    },
    onNavigateBack = { navController.popBackStack() }
)
```

**IMPORTANTE**:
- Todas las screens reciben lambdas de navegación como parámetros
- NUNCA pasar `navController` directamente a Composables
- Usar `Screen.*.createRoute()` para construir rutas con argumentos
- Extraer argumentos en el composable() del NavGraph, no en las screens

### UI State Management Pattern

Todas las screens siguen un patrón consistente de manejo de estado:

**1. Definir sealed class/interface para UI State:**
```kotlin
sealed interface HomeUiState {
    object Loading : HomeUiState
    object Empty : HomeUiState
    data class Success(val grupos: List<Grupo>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
```

**2. En ViewModel, exponer StateFlow:**
```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val grupoRepository: GrupoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }
}
```

**3. En Screen Composable, observar estado:**
```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is HomeUiState.Loading -> LoadingIndicator()
        is HomeUiState.Empty -> EmptyState()
        is HomeUiState.Success -> ContentList(state.grupos)
        is HomeUiState.Error -> ErrorMessage(state.message)
    }
}
```

**Beneficios de este patrón:**
- Estado unidireccional (one-way data flow)
- Fácil testing de ViewModels
- UI declarativa que reacciona a cambios de estado
- Type-safe con sealed classes

## Current Implementation Status

### ✅ Completed (Fases 1-4: Foundation, UI, Reports & Auth)
- Room Database con todas las entidades (Docente, Grupo, Estudiante, RegistroAsistencia)
- DAOs con queries optimizadas usando Flow
- Repositories con patrón repository
- Hilt/Dagger configurado (DatabaseModule, RepositoryModule)
- Type Converters para enums
- Material 3 Theme personalizado (Light/Dark mode)
- Sistema de navegación (Navigation Compose) con NavGraph completo
- **LoginScreen** para autenticación de docentes ✅
- **RegisterScreen** para registro de nuevos docentes ✅
- **DocenteProfileScreen** para gestión de perfil ✅
- **HomeScreen** con lista de grupos y estadísticas ✅
- **GroupDetailScreen** con gestión de estudiantes ✅
- **AddEditGroupScreen** para CRUD de grupos ✅
- **AddEditStudentScreen** para CRUD de estudiantes ✅
- **AttendanceScreen** para toma de asistencia (PANTALLA CRÍTICA) ⭐ ✅
- **ReportsScreen** para visualización y exportación de reportes ✅
- **SettingsScreen** para configuración de la app ✅
- ViewModels para todas las pantallas (10 ViewModels) ✅
- Componentes reutilizables (GrupoCard, EstudianteCard, EstudianteAttendanceCard) ✅
- **PdfGenerator** con iText7 para reportes PDF ✅
- **ExcelGenerator** con Apache POI para exportar a Excel ✅
- **ImageHandler** para captura y gestión de fotos ✅
- **ValidationUtils** para validación de formularios ✅
- **AuthHelper** para lógica de autenticación ✅
- **LocaleHelper** para internacionalización ✅
- **SampleDataGenerator** para datos de prueba ✅

### 🔨 En Progreso
- Testing exhaustivo de todas las funcionalidades
- Optimizaciones de performance
- Documentación final

### 📋 Pendiente (Fase 5: Polish & Testing)
- Tests unitarios completos para ViewModels
- Tests de integración para DAOs
- UI tests para flujos críticos
- Optimización de AttendanceScreen con muchos estudiantes
- Animaciones y feedback háptico
- Soporte para tablets/landscape
- Tutorial de primera vez (onboarding)

**Estado actual**: La aplicación está completamente funcional con todas las pantallas implementadas. Los usuarios pueden registrarse, autenticarse, crear grupos, agregar estudiantes, tomar asistencia y generar reportes en PDF/Excel. El sistema de fotos y validaciones está operativo. Falta principalmente testing exhaustivo y optimizaciones finales.

## Development Notes

### Groovy DSL Configuration

**IMPORTANTE**: Este proyecto usa **Groovy DSL** para archivos Gradle:
- `build.gradle` (Project level)
- `build.gradle` (Module: app)
- `settings.gradle`

NO usar Kotlin DSL (`.gradle.kts`). Seguir la sintaxis Groovy tradicional.

### Room Database Best Practices

1. **Queries**: Usar `Flow` para observar cambios en tiempo real
2. **Suspend functions**: Para operaciones de escritura (insert/update/delete)
3. **Indices**: Definidos para optimizar queries frecuentes
4. **Foreign Keys**: Configuradas con `CASCADE` para mantener integridad
5. **Type Converters**: Ya configurados para enums (`EstadoAsistencia`)

**KAPT Configuration**: El proyecto usa KAPT para Room y Hilt. En `build.gradle` está configurado:
- `room.schemaLocation` para exportar esquemas
- `correctErrorTypes = true` para referencias a clases generadas

### Compose Development

- Toda la UI está en Jetpack Compose (no XML layouts)
- Usar Material 3 components
- Theme personalizado en `ui/theme/`
- Soporta dynamic color en Android 12+
- Modo oscuro/claro

**Nota**: ViewBinding y DataBinding están habilitados en `build.gradle` pero NO se usan, ya que toda la UI es Compose. Pueden deshabilitarse si es necesario.

### File Sharing

Configurado FileProvider en `AndroidManifest.xml`:
- Authority: `${applicationId}.fileprovider`
- Paths: `res/xml/file_paths.xml`
- Para compartir PDFs/Excel generados

### Date Handling

Usar `DateUtils` para todas las operaciones de fecha:
- `getStartOfDay()`: Timestamp de 00:00:00
- `getTodayStart()`: Inicio del día actual
- `formatDate()`: Convertir timestamp a String
- `isSameDay()`: Comparar fechas

Fechas se almacenan como `Long` (timestamp) en la BD.

### Common Implementation Patterns

**Cargar datos relacionados (Join queries):**
Las queries complejas con joins ya están implementadas en los DAOs. Ejemplo:
```kotlin
// En RegistroAsistenciaDao
@Query("""
    SELECT r.* FROM registros_asistencia r
    INNER JOIN estudiantes e ON r.estudianteId = e.id
    WHERE e.grupoId = :grupoId AND r.fecha = :fecha
""")
fun getRegistrosPorGrupoYFecha(grupoId: Long, fecha: Long): Flow<List<RegistroAsistencia>>
```

**Validaciones de negocio:**
Implementar en Repository, NO en ViewModel:
```kotlin
// ✅ CORRECTO - En Repository
suspend fun guardarEstudiante(estudiante: Estudiante): Result<Long> {
    if (estudiante.codigo.isBlank()) return Result.failure(Exception("Código requerido"))
    // ... más validaciones
    return try {
        Result.success(estudianteDao.insert(estudiante))
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

**Manejo de errores:**
Usar `try-catch` en ViewModels y actualizar UI state:
```kotlin
viewModelScope.launch {
    try {
        _uiState.value = UiState.Loading
        val result = repository.loadData()
        _uiState.value = UiState.Success(result)
    } catch (e: Exception) {
        _uiState.value = UiState.Error(e.message ?: "Error desconocido")
    }
}
```

**Formularios con validación:**
Las screens de AddEdit ya implementan el patrón:
- Estado local con `remember { mutableStateOf() }` para cada campo
- Validación al hacer submit
- Mostrar errores con `isError` en TextField
- Deshabilitar botón guardar si hay campos inválidos

## Funcionalidades Principales

### 1. Sistema de Autenticación ✅

**Implementado:**
- ✅ LoginScreen con validación de credenciales
- ✅ RegisterScreen para registro de nuevos docentes
- ✅ DocenteProfileScreen para gestión de perfil
- ✅ AuthHelper con utilidades de autenticación
- ✅ Validación de email y contraseñas
- ✅ Persistencia de sesión
- ✅ Integración completa con Room Database

**Ubicación**: `ui/screens/login/`, `ui/screens/register/`, `ui/screens/profile/`, `utils/AuthHelper.kt`

### 2. Registro de Asistencia ⭐ (MÁS CRÍTICA) ✅

**Implementado:**
- ✅ Lista de estudiantes del grupo con scroll optimizado
- ✅ Botones rápidos: Presente/Ausente/Tardanza/Justificado
- ✅ Guardado en tiempo real con Room
- ✅ Validación: constraint único (estudianteId, fecha) previene duplicados
- ✅ Selector de fecha para registrar asistencia de días anteriores
- ✅ Indicadores visuales de estado con colores Material 3
- ✅ EstudianteAttendanceCard como componente reutilizable

**Ubicación**: `ui/screens/attendance/AttendanceScreen.kt`

### 3. Gestión de Grupos y Estudiantes ✅

**Implementado:**
- ✅ Crear/Editar grupos (AddEditGroupScreen)
- ✅ Ver detalle de grupo con lista de estudiantes (GroupDetailScreen)
- ✅ Agregar/Editar estudiantes (AddEditStudentScreen)
- ✅ Validación de campos requeridos con ValidationUtils
- ✅ Códigos únicos de estudiante
- ✅ Captura y gestión de fotos con ImageHandler
- ✅ Navegación fluida entre pantallas

**Pendiente:**
- Eliminar grupos/estudiantes (soft delete con campo `activo`)
- Transferir estudiantes entre grupos (función existe en Repository)
- Búsqueda y filtros avanzados

**Ubicación**: `ui/screens/groups/`, `ui/screens/students/`

### 4. Reportes y Exportación ✅

**Implementado:**
- ✅ ReportsScreen con visualización de estadísticas
- ✅ Selector de grupo y rango de fechas
- ✅ PdfGenerator con iText7 para reportes PDF profesionales
- ✅ ExcelGenerator con Apache POI para exportar datos
- ✅ Sistema de compartir archivos via FileProvider
- ✅ Formato profesional con tablas y encabezados

**Pendiente:**
- Gráficos con MPAndroidChart (dependencia instalada)
- Reportes personalizables con más opciones

**Ubicación**: `ui/screens/reports/ReportsScreen.kt`, `utils/PdfGenerator.kt`, `utils/ExcelGenerator.kt`

### 5. Dashboard/Home ✅

**Implementado:**
- ✅ Lista de grupos activos con GrupoCard
- ✅ Contador de grupos
- ✅ FAB para crear nuevo grupo
- ✅ Acceso rápido a toma de asistencia desde cada grupo
- ✅ Navegación a detalles de grupo
- ✅ Estados: Loading, Empty, Success, Error
- ✅ Pull-to-refresh

**Ubicación**: `ui/screens/home/HomeScreen.kt`

### 6. Configuración ✅

**Implementado:**
- ✅ SettingsScreen con opciones de configuración
- ✅ LocaleHelper para cambio de idioma
- ✅ Preferencias persistentes con DataStore

**Ubicación**: `ui/screens/settings/SettingsScreen.kt`, `utils/LocaleHelper.kt`

## Testing

### Development Testing
- **SampleDataGenerator**: Genera datos de prueba para desarrollo
  - Ubicación: `utils/SampleDataGenerator.kt`
  - Útil para poblar BD durante desarrollo y testing
  - Crea docentes, grupos, estudiantes y registros de asistencia de ejemplo

### Unit Tests
- ViewModels: Usar `androidx.arch.core:core-testing`
- Repositories: Mock DAOs
- Utils: JUnit tests (ValidationUtils, DateUtils, etc.)

### Integration Tests
- DAOs: `@RunWith(AndroidJUnit4::class)`
- Database migrations
- Repository integration tests

### UI Tests
- Compose UI tests con `ui-test-junit4`
- Tests para flujos críticos (login, registro asistencia)

### Internationalization

Usar `LocaleHelper` para manejo de idiomas:
- Soporte multi-idioma implementado
- Ubicación: `utils/LocaleHelper.kt`
- Configuración persistente con DataStore

## Próximos Pasos (TODO)

### Fase 5: Testing & Quality Assurance (ALTA PRIORIDAD)

1. **Testing Exhaustivo**
   - Unit tests para todos los ViewModels
   - Unit tests para Repositories
   - Integration tests para DAOs
   - UI tests para flujos críticos (login, asistencia, reportes)
   - Tests de ValidationUtils y DateUtils
   - Tests de generadores PDF/Excel

2. **Testing de Integración**
   - Flujo completo de registro → login → crear grupo → agregar estudiantes → tomar asistencia
   - Generación y compartir de reportes
   - Captura y visualización de fotos
   - Cambio de idioma y persistencia

3. **Verificación de Permisos**
   - Testing de runtime permissions en diferentes APIs (26-34)
   - Camera permission flow
   - Storage permission flow
   - Manejo de rechazos de permisos

### Fase 6: Performance & Optimization (MEDIA PRIORIDAD)

4. **Optimizaciones de Performance**
   - Revisar performance de AttendanceScreen con 50+ estudiantes
   - Paginación en listas largas si es necesario
   - Optimización de queries Room
   - Indices adicionales en BD si se detectan queries lentas
   - LazyColumn optimizations

5. **Memory & Storage**
   - Optimización de carga de imágenes
   - Compresión de fotos antes de guardar
   - Limpieza de archivos temporales
   - Gestión de cache

### Fase 7: UX & Polish (BAJA PRIORIDAD)

6. **Mejoras de UX**
   - Animaciones de transición entre screens
   - Feedback háptico en acciones importantes
   - Confirmaciones antes de acciones destructivas
   - Loading states más informativos
   - Error messages más descriptivos

7. **Mejoras Visuales**
   - Pulir diseño según video de referencia
   - Soporte para tablets/landscape
   - Responsive design para diferentes tamaños de pantalla
   - Dark mode optimizations

8. **Features Adicionales**
   - Tutorial de primera vez (onboarding)
   - Búsqueda y filtros avanzados en listas
   - Exportación de múltiples grupos a la vez
   - Estadísticas avanzadas con gráficos (MPAndroidChart)
   - Backup y restore de base de datos

9. **Documentación**
   - Manual de usuario
   - FAQs
   - Guía de troubleshooting
   - Video tutoriales (opcional)

## Permissions

Permisos declarados en `AndroidManifest.xml`:
- `WRITE_EXTERNAL_STORAGE` (API ≤32)
- `READ_EXTERNAL_STORAGE` (API ≤32)
- `READ_MEDIA_IMAGES` (API 33+)
- `CAMERA` (fotos de estudiantes)
- `INTERNET` (futuras sincronizaciones)

## Troubleshooting

### Build Errors

**KAPT errors con Room/Hilt**:
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

**"Unresolved reference" para clases generadas**:
- Hacer rebuild del proyecto: `./gradlew clean build`
- Verificar que KAPT esté habilitado en `app/build.gradle`
- Revisar que las anotaciones sean correctas (@Entity, @Dao, @Database, @HiltViewModel)
- En Android Studio: Build → Rebuild Project

**Dependencias no resueltas**:
```bash
./gradlew dependencies
# Verificar que todos los repositorios estén accesibles (Google, Maven Central, JitPack)
# Verificar conexión a Internet
# Probar con: ./gradlew build --refresh-dependencies
```

**Gradle sync failures**:
```bash
# Si Gradle no sincroniza en Android Studio:
./gradlew --stop  # Detener Gradle daemon
./gradlew build   # Reiniciar daemon y rebuildd
# O borrar caches: File → Invalidate Caches / Restart en Android Studio
```

**"Execution failed for task ':app:kaptDebugKotlin'"**:
- Error común al cambiar entidades Room sin incrementar versión DB
- Verificar que la versión en `AppDatabase.kt` sea correcta (actualmente: 1)
- Si se modificó schema, incrementar versión o usar fallbackToDestructiveMigration en desarrollo

### Runtime Issues

**Hilt DI no funciona**:
- Verificar que `MainApplication` tenga `@HiltAndroidApp`
- Verificar que Activities/Fragments tengan `@AndroidEntryPoint`
- Rebuild el proyecto para generar código de Hilt

**Room database crashes**:
- Verificar que las migraciones estén implementadas si cambió el schema
- Revisar `app/schemas/` para ver el schema exportado
- En desarrollo: puede usar `.fallbackToDestructiveMigration()` (⚠️ borra datos)

## Resources

- **Prototipo UI**: https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit
- **Material Design 3**: https://m3.material.io/
- **Jetpack Compose**: https://developer.android.com/jetpack/compose
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Hilt**: https://developer.android.com/training/dependency-injection/hilt-android

## Important Notes

- La app debe funcionar **100% offline** (offline-first)
- Los datos de estudiantes son información sensible
- Priorizar performance en la pantalla de toma de asistencia
- Usar español para strings de usuario
- Código limpio con comentarios en español
