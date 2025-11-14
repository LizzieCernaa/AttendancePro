# INFORMACIÓN GLOBAL DE LA APLICACIÓN - AsisteDocente

Este documento contiene toda la información de configuración, versiones, credenciales iniciales y datos relevantes de la aplicación AsisteDocente.

---

## 1. INFORMACIÓN BÁSICA

### Identificación de la App
- **Nombre de la Aplicación**: AsisteDocente
- **Nombre Completo**: Asistencia Docente / Attendance Teacher
- **Package Name**: `sv.edu.catolica.asistedocente`
- **Version Code**: 1
- **Version Name**: 1.0
- **Idioma Principal**: Español (con soporte para internacionalización)

### Plataforma
- **Plataforma**: Android nativo
- **Lenguaje**: Kotlin 1.9.22
- **Framework UI**: Jetpack Compose
- **Build Tool**: Gradle 8.2.2 con Groovy DSL

---

## 2. VERSIONES Y COMPATIBILIDAD

### SDK Versions
- **Minimum SDK**: 26 (Android 8.0 Oreo)
  - **Razón**: Requerido por dependencias Apache POI y Log4j para generación de Excel
  - **Cobertura**: ~95% de dispositivos Android activos
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

### Versión de Java
- **Java Version**: 17
- **JVM Target**: 17
- **Core Library Desugaring**: Habilitado (para compatibilidad con APIs modernas)

### Versiones de Dependencias Principales
- **Room Database**: 2.6.1
- **Hilt (Dependency Injection)**: 2.50
- **Jetpack Compose**: 1.6.0
- **Material 3**: 1.2.0
- **Kotlin Coroutines**: 1.7.3
- **Navigation Compose**: 2.7.6
- **Lifecycle**: 2.7.0
- **iText7 (PDF)**: 7.2.5
- **Apache POI (Excel)**: 5.2.3
- **Coil (Images)**: 2.5.0
- **MPAndroidChart**: v3.1.0
- **WorkManager**: 2.9.0
- **DataStore**: 1.0.0

---

## 3. BASE DE DATOS

### Configuración de Room Database
- **Nombre de la Base de Datos**: `asiste_docente_db`
- **Versión de la Base de Datos**: 1
- **Ubicación**: Internal Storage de la app
- **Modo**: Offline-First (100% local, sin servidor remoto)
- **Exportar Schemas**: Habilitado
- **Schema Location**: `app/schemas/`

### Tablas Principales
1. **docentes** - Información de los docentes (usuarios)
2. **grupos** - Grupos/clases creados por docentes
3. **estudiantes** - Estudiantes de cada grupo
4. **registros_asistencia** - Registros diarios de asistencia

---

## 4. USUARIOS Y CREDENCIALES INICIALES

### Usuario de Prueba (Development)

**IMPORTANTE**: Para testing y desarrollo, usar `SampleDataGenerator.kt` para crear datos de prueba.

#### Docente Demo 1
- **Nombre**: Juan
- **Apellido**: Pérez
- **Email**: `juan.perez@catolica.edu.sv`
- **Password**: `demo123` (en desarrollo, sin hash)
- **Teléfono**: 2222-3333
- **Rol**: Docente
- **Estado**: Activo

#### Docente Demo 2
- **Nombre**: María
- **Apellido**: González
- **Email**: `maria.gonzalez@catolica.edu.sv`
- **Password**: `demo123`
- **Teléfono**: 2222-4444
- **Rol**: Docente
- **Estado**: Activo

### Generar Datos de Prueba

Para poblar la base de datos con datos de ejemplo:

```kotlin
// En cualquier ViewModel o inicialización
val sampleDataGenerator = SampleDataGenerator(
    docenteRepository,
    grupoRepository,
    estudianteRepository,
    asistenciaRepository
)

viewModelScope.launch {
    sampleDataGenerator.generateAllSampleData()
}
```

**Esto creará:**
- 2 docentes de ejemplo
- 3 grupos por docente (6 grupos total)
- 15-25 estudiantes por grupo
- Registros de asistencia de los últimos 30 días

### Credenciales de Producción

**⚠️ IMPORTANTE PARA PRODUCCIÓN:**
1. Cambiar las contraseñas por defecto
2. Implementar hashing de contraseñas (bcrypt, Argon2)
3. No incluir credenciales hardcoded en el código
4. Usar variables de entorno o configuración segura
5. Implementar recuperación de contraseña

---

## 5. PERMISOS DE LA APLICACIÓN

### Permisos Declarados en AndroidManifest.xml

#### Permisos de Almacenamiento
```xml
<!-- Android ≤ 12 (API 32) -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />

<!-- Android 13+ (API 33+) -->
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
```

**Uso**: Para guardar y compartir reportes PDF/Excel y fotos de estudiantes

#### Permiso de Cámara
```xml
<uses-permission android:name="android.permission.CAMERA" />
```

**Uso**: Para capturar fotos de perfil de estudiantes

#### Permiso de Internet
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

**Uso**: Reservado para futuras sincronizaciones en la nube (actualmente no se usa)

### Permisos en Runtime (Dangerous Permissions)

Los siguientes permisos requieren solicitud explícita al usuario:
- `CAMERA` - Solicitar antes de capturar foto
- `READ_EXTERNAL_STORAGE` (API ≤32)
- `WRITE_EXTERNAL_STORAGE` (API ≤32)
- `READ_MEDIA_IMAGES` (API 33+)

**Implementación**: Ver `utils/ImageHandler.kt` para manejo de permisos

---

## 6. CONFIGURACIÓN DE FILE PROVIDER

### Autoridad
- **Authority**: `sv.edu.catolica.asistedocente.fileprovider`

### Paths Configurados (`res/xml/file_paths.xml`)
```xml
<paths>
    <files-path name="pdf_files" path="reports/pdf/" />
    <files-path name="excel_files" path="reports/excel/" />
    <cache-path name="shared_files" path="shared/" />
    <external-files-path name="external_files" path="." />
</paths>
```

### Uso
- **PDFs**: Se guardan en `reports/pdf/`
- **Excel**: Se guardan en `reports/excel/`
- **Archivos temporales**: Cache directory
- **Fotos**: External files directory

---

## 7. CONFIGURACIÓN DE BUILD

### Build Types

#### Debug (Default)
```groovy
debug {
    applicationIdSuffix ".debug"
    debuggable true
    minifyEnabled false
}
```

#### Release
```groovy
release {
    minifyEnabled false
    proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
}
```

**Nota**: ProGuard/R8 está deshabilitado actualmente. Para producción, habilitar `minifyEnabled true`.

### Signing Config

**⚠️ IMPORTANTE**: Antes de publicar en producción:
1. Generar keystore de release
2. Configurar signing config en `build.gradle`
3. Nunca incluir keystore en control de versiones
4. Usar variables de entorno para credenciales

---

## 8. CARACTERÍSTICAS DE LA APLICACIÓN

### Funcionalidades Principales Implementadas

1. **Autenticación y Registro** ✅
   - Login con email/password
   - Registro de nuevos docentes
   - Gestión de perfil
   - Persistencia de sesión

2. **Gestión de Grupos** ✅
   - Crear/editar grupos
   - Asignar materia y horario
   - Ver detalle de grupo
   - Listar estudiantes del grupo

3. **Gestión de Estudiantes** ✅
   - Agregar/editar estudiantes
   - Código único por estudiante
   - Captura de foto de perfil
   - Validación de datos

4. **Toma de Asistencia** ⭐ ✅ (PANTALLA CRÍTICA)
   - Registro rápido: Presente/Ausente/Tardanza/Justificado
   - Selector de fecha
   - Guardado en tiempo real
   - Vista de lista optimizada

5. **Reportes y Exportación** ✅
   - Generación de PDF con iText7
   - Exportación a Excel con Apache POI
   - Selector de rango de fechas
   - Compartir archivos via Intent

6. **Configuración** ✅
   - Cambio de idioma
   - Preferencias persistentes
   - Tema claro/oscuro

### Características Técnicas

- **Arquitectura**: MVVM (Model-View-ViewModel)
- **Inyección de Dependencias**: Hilt/Dagger
- **Navegación**: Navigation Compose
- **Estado UI**: StateFlow con sealed classes
- **Base de Datos**: Room con Flow reactivo
- **Concurrencia**: Kotlin Coroutines
- **UI**: Jetpack Compose con Material 3
- **Tema**: Soporte para modo claro/oscuro
- **Imágenes**: Carga con Coil

---

## 9. CONFIGURACIÓN REGIONAL E INTERNACIONALIZACIÓN

### Idiomas Soportados
- **Español** (Principal)
- **Inglés** (Preparado para expansión)

### Configuración de Locale
- **Helper**: `utils/LocaleHelper.kt`
- **Persistencia**: DataStore Preferences
- **Cambio dinámico**: Sin necesidad de reiniciar app

### Strings
- **Ubicación**: `app/src/main/res/values/strings.xml`
- **Español**: `values-es/strings.xml`
- **Inglés**: `values/strings.xml` (default)

---

## 10. CONFIGURACIÓN DE TESTING

### Testing Frameworks Configurados

#### Unit Tests
- **JUnit**: 4.13.2
- **Truth**: 1.1.5 (assertions)
- **Coroutines Test**: 1.7.3
- **Architecture Core Testing**: 2.2.0 (LiveData/ViewModel)

#### Integration Tests
- **AndroidX Test**: 1.1.5
- **Espresso**: 3.5.1
- **Compose UI Test**: 1.6.0

### Datos de Prueba
- **Generador**: `utils/SampleDataGenerator.kt`
- **Uso**: Crear datos realistas para testing manual y automatizado

---

## 11. CONFIGURACIÓN DE LOGGING Y DEBUGGING

### Logs en Desarrollo
```kotlin
// Usar Log estándar de Android
Log.d("TAG", "Debug message")
Log.e("TAG", "Error message", exception)
```

### Timber (Opcional - no instalado actualmente)
Si se desea agregar logging mejorado, instalar Timber:
```groovy
implementation 'com.jakewharton.timber:timber:5.0.1'
```

---

## 12. RENDIMIENTO Y OPTIMIZACIÓN

### Configuraciones Actuales

#### Memory
- **Heap Size**: Default Android
- **Large Heap**: No habilitado

#### Networking
- No hay llamadas de red actualmente (app 100% offline)

#### Database
- **WAL Mode**: Habilitado por defecto en Room
- **Query Optimization**: Índices en campos de búsqueda frecuente

#### UI
- **Compose**: Recomposición optimizada
- **LazyColumn**: Para listas largas
- **Coil**: Cache de imágenes habilitado

### Recomendaciones para Producción
1. Habilitar ProGuard/R8 para reducir tamaño APK
2. Implementar paginación en listas con más de 100 elementos
3. Comprimir fotos antes de guardar
4. Implementar Work Manager para tareas pesadas en background

---

## 13. URLS Y REFERENCIAS

### Documentación y Diseño
- **Video de Referencia Principal**: https://streamable.com/hya7nq
- **Prototipo Canva**: https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit
- **Repositorio**: (Agregar URL del repositorio Git si aplica)

### APIs y Recursos Externos
- **iText Documentation**: https://itextpdf.com/en/resources/api-documentation/itext-7
- **Apache POI**: https://poi.apache.org/
- **Material Design 3**: https://m3.material.io/
- **Compose Documentation**: https://developer.android.com/jetpack/compose

---

## 14. DEPLOYMENT Y DISTRIBUCIÓN

### Debug Build (Testing)
```bash
# Generar APK debug
./gradlew assembleDebug

# Ubicación del APK
app/build/outputs/apk/debug/app-debug.apk

# Instalar en dispositivo
./gradlew installDebug
```

### Release Build (Producción)

**Antes de crear release:**
1. Actualizar `versionCode` y `versionName` en `build.gradle`
2. Configurar signing con keystore
3. Habilitar ProGuard/R8 (`minifyEnabled true`)
4. Probar exhaustivamente

```bash
# Generar APK release
./gradlew assembleRelease

# Ubicación del APK
app/build/outputs/apk/release/app-release.apk
```

### Publicación en Google Play Store

**Requisitos:**
1. Cuenta de Google Play Developer ($25 USD one-time)
2. APK firmado con keystore de producción
3. Iconos y screenshots
4. Descripción de la app
5. Política de privacidad (URL)
6. Edad objetivo: Everyone (contenido educativo)
7. Permisos explicados claramente

---

## 15. MANTENIMIENTO Y SOPORTE

### Actualizaciones de Dependencias

Verificar actualizaciones regularmente:
```bash
./gradlew dependencyUpdates
```

### Backup de Base de Datos

**CRÍTICO**: Implementar sistema de backup automático antes de producción:
- Exportar DB a archivo cifrado
- Subir a Drive/Dropbox del usuario
- Restaurar desde backup

### Monitoreo (Futuro)

Para producción, considerar:
- **Firebase Crashlytics**: Reportes de crashes
- **Firebase Analytics**: Uso de la app
- **Performance Monitoring**: Detectar problemas de performance

---

## 16. SEGURIDAD

### Consideraciones Actuales

⚠️ **IMPORTANTE - MEJORAR ANTES DE PRODUCCIÓN:**

1. **Contraseñas**: Actualmente sin hashing. Implementar bcrypt/Argon2
2. **Cifrado de DB**: SQLCipher para cifrar base de datos completa
3. **Backup Cifrado**: No almacenar backups sin cifrar
4. **Validación**: Ya implementada con ValidationUtils
5. **SQL Injection**: Protegido por Room (prepared statements)
6. **Permisos**: Solicitar solo cuando sea necesario

### Datos Sensibles

**Información que contiene la app:**
- Nombres completos de estudiantes
- Emails
- Fotos
- Registros de asistencia
- Información de docentes

**Cumplimiento:**
- FERPA (Family Educational Rights and Privacy Act) - USA
- GDPR (si hay usuarios en EU)
- Ley de Protección de Datos local

---

## 17. CONTACTO Y SOPORTE

### Desarrollador
- **Institución**: Universidad Católica de El Salvador
- **Proyecto**: AsisteDocente v1.0
- **Año**: 2024

### Soporte Técnico
- Para bugs y issues: (Agregar email de soporte)
- Para sugerencias: (Agregar formulario o email)

---

## 18. CHANGELOG

### Versión 1.0 (Actual)
- ✅ Implementación completa de autenticación
- ✅ CRUD de grupos y estudiantes
- ✅ Toma de asistencia con 4 estados
- ✅ Generación de reportes PDF y Excel
- ✅ Captura de fotos de estudiantes
- ✅ Sistema de navegación completo
- ✅ Tema Material 3 con modo oscuro
- ✅ Soporte de internacionalización

### Versión 0.1 (Beta - no lanzada)
- ✅ Base de datos Room
- ✅ Estructura MVVM
- ✅ Inyección de dependencias Hilt
- ✅ Pantallas principales

---

## 19. LICENCIA

(Agregar información de licencia según aplique)
- MIT License
- Apache License 2.0
- GPL
- Propietario

---

## 20. NOTAS FINALES

### Para Nuevos Desarrolladores

1. **Leer primero**: `CLAUDE.md` para entender la arquitectura
2. **Ver video**: https://streamable.com/hya7nq para entender el flujo
3. **Revisar schema**: `DATABASE_SCHEMA.md` para la estructura de datos
4. **Generar datos**: Usar `SampleDataGenerator` para testing
5. **Seguir patrones**: Mantener consistencia con código existente

### Comandos Rápidos
```bash
# Build completo
./gradlew clean build

# Instalar y correr
./gradlew installDebug

# Ver logs
adb logcat | grep "AsisteDocente"

# Inspeccionar DB
./gradlew assembleDebug
# Luego usar Database Inspector en Android Studio
```

---

**Última actualización**: Noviembre 2024
**Versión del documento**: 1.0
