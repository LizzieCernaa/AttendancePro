<<<<<<< HEAD
# AttendancePro 📱

**AsisteDocente** (AttendancePro) es una aplicación móvil nativa para Android que permite a los docentes gestionar la asistencia de estudiantes de forma rápida y eficiente desde sus dispositivos móviles.
=======
# AttendancePro
App Movil con Adroid Studio📱

AsisteDocente (AttendancePro) es una aplicación móvil nativa para Android que permite a los docentes gestionar la asistencia de estudiantes de forma rápida y eficiente desde sus dispositivos móviles.
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

![Platform](https://img.shields.io/badge/Platform-Android-green.svg)
![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)
![MinSDK](https://img.shields.io/badge/MinSDK-26-orange.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

<<<<<<< HEAD
## 🎯 Características Principales

- ✅ **Autenticación de Docentes**: Sistema de login y registro seguro
- 📚 **Gestión de Grupos**: Crear y administrar grupos/clases
- 👥 **Gestión de Estudiantes**: Agregar estudiantes con fotos y códigos únicos
- ✏️ **Toma de Asistencia Rápida**: Registro con 4 estados (Presente, Ausente, Tardanza, Justificado)
- 📊 **Reportes PDF y Excel**: Generación y exportación de reportes profesionales
- 📸 **Captura de Fotos**: Fotos de perfil para estudiantes
- 🌙 **Tema Claro/Oscuro**: Interfaz moderna con Material Design 3
- 🌐 **Multiidioma**: Soporte para español e inglés
- 💾 **100% Offline**: Funciona completamente sin conexión a internet

## 📱 Capturas de Pantalla

[Video Demo Completo](https://streamable.com/hya7nq)

## 🛠️ Tecnologías Utilizadas

### Core
- **Kotlin** 1.9.22
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Diseño siguiendo las últimas guías de Material

### Arquitectura
- **MVVM** (Model-View-ViewModel)
- **Room Database** - Persistencia local con SQLite
- **Hilt/Dagger** - Inyección de dependencias
- **Kotlin Coroutines + Flow** - Programación asíncrona y reactiva
- **Navigation Compose** - Navegación entre pantallas

### Generación de Reportes
- **iText7** 7.2.5 - Generación de PDFs profesionales
- **Apache POI** 5.2.3 - Exportación a Excel

### Otros
- **Coil** - Carga de imágenes
- **DataStore** - Preferencias de usuario
- **WorkManager** - Tareas en background

## 📋 Requisitos

- **Android Studio**: Hedgehog (2023.1.1) o superior
- **JDK**: 17
- **Gradle**: 8.2.2
- **Android SDK**:
  - Min SDK: 26 (Android 8.0 Oreo)
  - Target SDK: 34 (Android 14)

## 🚀 Instalación y Configuración

### 1. Clonar el Repositorio
=======
🎯 Características Principales

- ✅ Autenticación de Docentes: Sistema de login y registro seguro
- 📚 Gestión de Grupos: Crear y administrar grupos/clases
- 👥 Gestión de Estudiantes: Agregar estudiantes con fotos y códigos únicos
- ✏️ Toma de Asistencia Rápida: Registro con 4 estados (Presente, Ausente, Tardanza, Justificado)
- 📊 Reportes PDF y Excel: Generación y exportación de reportes profesionales
- 📸 Captura de Fotos: Fotos de perfil para estudiantes
- 🌙 Tema Claro/Oscuro: Interfaz moderna con Material Design 3
- 🌐 Multiidioma: Soporte para español e inglés
- 💾 100% Offline: Funciona completamente sin conexión a internet

📱 Capturas de Pantalla

[Video Demo Completo](https://streamable.com/hya7nq)


🛠️ Tecnologías Utilizadas
Core
- Kotlin 1.9.22
- Jetpack Compose - UI moderna y declarativa
- Material Design 3 - Diseño siguiendo las últimas guías de Material

Arquitectura
- MVVM (Model-View-ViewModel)
- Room Database - Persistencia local con SQLite
- Hilt/Dagger - Inyección de dependencias
- Kotlin Coroutines + Flow - Programación asíncrona y reactiva
- Navigation Compose - Navegación entre pantallas

Generación de Reportes
- iText7 7.2.5 - Generación de PDFs profesionales
- Apache POI 5.2.3 - Exportación a Excel

Otros
- Coil - Carga de imágenes
- DataStore - Preferencias de usuario
- WorkManager - Tareas en background

📋 Requisitos

- Android Studio: Hedgehog (2023.1.1) o superior
- JDK: 17
- Gradle: 8.2.2
- Android SDK:
  - Min SDK: 26 (Android 8.0 Oreo)
  - Target SDK: 34 (Android 14)

🚀 Instalación y Configuración

1. Clonar el Repositorio
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

```bash
git clone https://github.com/LizzieCernaa/AttendancePro.git
cd AttendancePro
```

<<<<<<< HEAD
### 2. Abrir en Android Studio
=======
2. Abrir en Android Studio
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

1. Abrir Android Studio
2. File → Open
3. Seleccionar la carpeta del proyecto
4. Esperar a que Gradle sincronice las dependencias

<<<<<<< HEAD
### 3. Compilar y Ejecutar

**Usando Android Studio:**
- Click en Run → Run 'app' (o presionar Shift+F10)

**Usando línea de comandos:**
=======
3. Compilar y Ejecutar

Usando Android Studio:
- Click en Run → Run 'app' (o presionar Shift+F10)

Usando línea de comandos:
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651
```bash
# En Git Bash (Windows)
./gradlew assembleDebug
./gradlew installDebug
```

<<<<<<< HEAD
## 📖 Documentación

- **[CLAUDE.md](CLAUDE.md)** - Guía completa de arquitectura y desarrollo
- **[DATABASE_SCHEMA.md](DATABASE_SCHEMA.md)** - Esquema detallado de la base de datos
- **[APP_INFO.md](APP_INFO.md)** - Información de configuración y versiones

## 🗄️ Estructura del Proyecto
=======
📖 Documentación

- [CLAUDE.md](CLAUDE.md) - Guía completa de arquitectura y desarrollo
- [DATABASE_SCHEMA.md](DATABASE_SCHEMA.md) - Esquema detallado de la base de datos
- [APP_INFO.md](APP_INFO.md) - Información de configuración y versiones

🗄️ Estructura del Proyecto
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

```
app/src/main/java/sv/edu/catolica/asistedocente/
├── data/
│   ├── local/
│   │   ├── dao/              # Data Access Objects
│   │   ├── entities/         # Room entities
│   │   └── database/         # Database configuration
│   └── repository/           # Repository pattern
├── di/                       # Dependency injection (Hilt)
├── ui/
│   ├── screens/              # Pantallas de la app
│   │   ├── login/
│   │   ├── register/
│   │   ├── profile/
│   │   ├── home/
│   │   ├── groups/
│   │   ├── students/
│   │   ├── attendance/       # ⭐ Pantalla crítica
│   │   ├── reports/
│   │   └── settings/
│   ├── components/           # Componentes reutilizables
│   ├── navigation/           # Sistema de navegación
│   └── theme/                # Material 3 Theme
└── utils/                    # Utilidades (PDF, Excel, validaciones, etc.)
```

<<<<<<< HEAD
## 🧪 Testing

### Ejecutar Tests Unitarios
=======
🧪 Testing

Ejecutar Tests Unitarios
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651
```bash
./gradlew test
```

<<<<<<< HEAD
### Ejecutar Tests Instrumentados
=======
Ejecutar Tests Instrumentados
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651
```bash
./gradlew connectedAndroidTest
```

<<<<<<< HEAD
### Generar Datos de Prueba
La app incluye `SampleDataGenerator` para crear datos de prueba. Ver `APP_INFO.md` para credenciales de testing.

## 📦 Build

### Debug Build
=======
Generar Datos de Prueba
La app incluye `SampleDataGenerator` para crear datos de prueba. Ver `APP_INFO.md` para credenciales de testing.

📦 Build

Debug Build
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651
```bash
./gradlew assembleDebug
# APK en: app/build/outputs/apk/debug/app-debug.apk
```

<<<<<<< HEAD
### Release Build
=======
Release Build
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651
```bash
./gradlew assembleRelease
# APK en: app/build/outputs/apk/release/app-release.apk
```

<<<<<<< HEAD
## 🎨 Diseño
=======
🎨 Diseño
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

El diseño de la aplicación sigue las guías de Material Design 3 y está basado en:
- [Video de Referencia](https://streamable.com/hya7nq) - Funcionamiento completo
- [Prototipo Canva](https://www.canva.com/design/DAG1QTdvv0E/qdJwbjllXdA2tF9KD5R1ug/edit)

<<<<<<< HEAD
## 🤝 Contribuciones
=======
🤝 Contribuciones
>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

<<<<<<< HEAD
## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Autores

- **Lizzie Cerna** - [@LizzieCernaa](https://github.com/LizzieCernaa)

## 🙏 Agradecimientos

- Universidad Católica de El Salvador
- Comunidad de Android Developers
- Material Design Team

## 📞 Contacto

Para preguntas, sugerencias o reportar bugs:
- GitHub Issues: [https://github.com/LizzieCernaa/AttendancePro/issues](https://github.com/LizzieCernaa/AttendancePro/issues)

---
=======
👥 Autores

- Lizzie Cerna

>>>>>>> a348de390ac9b38d2d82c5dc249b5ff1d2403651

⭐ Si este proyecto te fue útil, no olvides darle una estrella!
