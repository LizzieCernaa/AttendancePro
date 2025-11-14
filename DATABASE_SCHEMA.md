# DATABASE SCHEMA - AsisteDocente

Este documento describe el esquema completo de la base de datos de la aplicación AsisteDocente.

## Información General

- **Tipo de Base de Datos**: SQLite (Android Room)
- **Nombre de la Base de Datos**: `asiste_docente_db`
- **Versión Actual**: 1
- **ORM**: Room Persistence Library 2.6.1
- **Arquitectura**: 100% Offline-First (sin servidor remoto)

## Ubicación de Archivos

- **Database Class**: `app/src/main/java/sv/edu/catolica/asistedocente/data/local/database/AppDatabase.kt`
- **Entities**: `app/src/main/java/sv/edu/catolica/asistedocente/data/local/entities/`
- **DAOs**: `app/src/main/java/sv/edu/catolica/asistedocente/data/local/dao/`
- **Schema Export**: `app/schemas/sv.edu.catolica.asistedocente.data.local.database.AppDatabase/1.json`

---

## Tablas de la Base de Datos

### 1. Tabla: `docentes`

Almacena la información de los docentes que utilizan la aplicación.

**Columnas:**

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID único del docente |
| nombre | TEXT | NOT NULL | Nombre del docente |
| apellido | TEXT | NOT NULL | Apellido del docente |
| email | TEXT | NOT NULL UNIQUE | Email (usado para login) |
| password | TEXT | NOT NULL | Contraseña (hash) |
| telefono | TEXT | NULL | Teléfono de contacto |
| foto | TEXT | NULL | URI de la foto de perfil |
| activo | INTEGER | NOT NULL DEFAULT 1 | 1 = activo, 0 = inactivo |
| fechaCreacion | INTEGER | NOT NULL | Timestamp de creación |

**Índices:**
- `index_docentes_email` (UNIQUE) - Para búsquedas rápidas por email

**Script SQL Equivalente:**

```sql
CREATE TABLE docentes (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    nombre TEXT NOT NULL,
    apellido TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    telefono TEXT,
    foto TEXT,
    activo INTEGER NOT NULL DEFAULT 1,
    fechaCreacion INTEGER NOT NULL
);

CREATE UNIQUE INDEX index_docentes_email ON docentes(email);
```

---

### 2. Tabla: `grupos`

Almacena los grupos/clases creados por los docentes.

**Columnas:**

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID único del grupo |
| nombre | TEXT | NOT NULL | Nombre del grupo (ej: "Matemática 101") |
| materia | TEXT | NOT NULL | Materia que se imparte |
| horario | TEXT | NULL | Horario de la clase |
| descripcion | TEXT | NULL | Descripción adicional |
| docenteId | INTEGER | NOT NULL, FK → docentes(id) | ID del docente propietario |
| activo | INTEGER | NOT NULL DEFAULT 1 | 1 = activo, 0 = inactivo |
| fechaCreacion | INTEGER | NOT NULL | Timestamp de creación |

**Foreign Keys:**
- `docenteId` REFERENCES `docentes(id)` ON DELETE CASCADE

**Índices:**
- `index_grupos_docenteId` - Para búsquedas rápidas por docente

**Script SQL Equivalente:**

```sql
CREATE TABLE grupos (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    nombre TEXT NOT NULL,
    materia TEXT NOT NULL,
    horario TEXT,
    descripcion TEXT,
    docenteId INTEGER NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    fechaCreacion INTEGER NOT NULL,
    FOREIGN KEY(docenteId) REFERENCES docentes(id) ON DELETE CASCADE
);

CREATE INDEX index_grupos_docenteId ON grupos(docenteId);
```

---

### 3. Tabla: `estudiantes`

Almacena la información de los estudiantes en cada grupo.

**Columnas:**

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID único del estudiante |
| nombre | TEXT | NOT NULL | Nombre del estudiante |
| apellido | TEXT | NOT NULL | Apellido del estudiante |
| codigo | TEXT | NOT NULL UNIQUE | Código/carnet único del estudiante |
| email | TEXT | NULL | Email del estudiante (opcional) |
| foto | TEXT | NULL | URI de la foto del estudiante |
| grupoId | INTEGER | NOT NULL, FK → grupos(id) | ID del grupo al que pertenece |
| activo | INTEGER | NOT NULL DEFAULT 1 | 1 = activo, 0 = inactivo |
| fechaCreacion | INTEGER | NOT NULL | Timestamp de creación |

**Foreign Keys:**
- `grupoId` REFERENCES `grupos(id)` ON DELETE CASCADE

**Índices:**
- `index_estudiantes_codigo` (UNIQUE) - Para códigos únicos
- `index_estudiantes_grupoId` - Para búsquedas rápidas por grupo

**Script SQL Equivalente:**

```sql
CREATE TABLE estudiantes (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    nombre TEXT NOT NULL,
    apellido TEXT NOT NULL,
    codigo TEXT NOT NULL UNIQUE,
    email TEXT,
    foto TEXT,
    grupoId INTEGER NOT NULL,
    activo INTEGER NOT NULL DEFAULT 1,
    fechaCreacion INTEGER NOT NULL,
    FOREIGN KEY(grupoId) REFERENCES grupos(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX index_estudiantes_codigo ON estudiantes(codigo);
CREATE INDEX index_estudiantes_grupoId ON estudiantes(grupoId);
```

---

### 4. Tabla: `registros_asistencia`

Almacena los registros diarios de asistencia de cada estudiante.

**Columnas:**

| Columna | Tipo | Restricciones | Descripción |
|---------|------|---------------|-------------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | ID único del registro |
| estudianteId | INTEGER | NOT NULL, FK → estudiantes(id) | ID del estudiante |
| grupoId | INTEGER | NOT NULL, FK → grupos(id) | ID del grupo |
| fecha | INTEGER | NOT NULL | Timestamp de la fecha (00:00:00) |
| estado | TEXT | NOT NULL | Estado: PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO |
| notas | TEXT | NULL | Notas adicionales sobre el registro |
| horaRegistro | INTEGER | NOT NULL | Timestamp exacto del registro |

**Foreign Keys:**
- `estudianteId` REFERENCES `estudiantes(id)` ON DELETE CASCADE
- `grupoId` REFERENCES `grupos(id)` ON DELETE CASCADE

**Índices:**
- `index_registros_estudianteId` - Para búsquedas por estudiante
- `index_registros_grupoId` - Para búsquedas por grupo
- `index_registros_fecha` - Para búsquedas por fecha

**Constraints Únicos:**
- `UNIQUE(estudianteId, fecha)` - Previene múltiples registros del mismo estudiante en la misma fecha

**Script SQL Equivalente:**

```sql
CREATE TABLE registros_asistencia (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    estudianteId INTEGER NOT NULL,
    grupoId INTEGER NOT NULL,
    fecha INTEGER NOT NULL,
    estado TEXT NOT NULL,
    notas TEXT,
    horaRegistro INTEGER NOT NULL,
    FOREIGN KEY(estudianteId) REFERENCES estudiantes(id) ON DELETE CASCADE,
    FOREIGN KEY(grupoId) REFERENCES grupos(id) ON DELETE CASCADE,
    UNIQUE(estudianteId, fecha)
);

CREATE INDEX index_registros_estudianteId ON registros_asistencia(estudianteId);
CREATE INDEX index_registros_grupoId ON registros_asistencia(grupoId);
CREATE INDEX index_registros_fecha ON registros_asistencia(fecha);
```

---

## Enums y Type Converters

### EstadoAsistencia

**Valores posibles:**
- `PRESENTE` - El estudiante asistió a clase
- `AUSENTE` - El estudiante no asistió
- `TARDANZA` - El estudiante llegó tarde
- `JUSTIFICADO` - Ausencia justificada

**Almacenamiento:** Como TEXT en la base de datos

**Type Converter:** `Converters.kt` convierte entre `EstadoAsistencia` enum y String

---

## Relaciones entre Tablas

```
docentes (1) ──┬──> (N) grupos
               │
               └──> (1) perfil actual

grupos (1) ────┬──> (N) estudiantes
               │
               └──> (N) registros_asistencia

estudiantes (1) ──> (N) registros_asistencia
```

### Cascade Delete

- **Si se elimina un docente**: Se eliminan todos sus grupos (y en cascada, todos los estudiantes y registros de esos grupos)
- **Si se elimina un grupo**: Se eliminan todos sus estudiantes y registros de asistencia
- **Si se elimina un estudiante**: Se eliminan todos sus registros de asistencia

**Nota:** En la práctica, se usa "soft delete" (campo `activo = 0`) en lugar de eliminación física para preservar historial.

---

## Queries Principales

### Queries de Docente
```sql
-- Obtener docente por email (login)
SELECT * FROM docentes WHERE email = ? AND activo = 1;

-- Obtener docente por ID
SELECT * FROM docentes WHERE id = ?;
```

### Queries de Grupo
```sql
-- Obtener todos los grupos de un docente
SELECT * FROM grupos WHERE docenteId = ? AND activo = 1 ORDER BY fechaCreacion DESC;

-- Obtener grupo por ID
SELECT * FROM grupos WHERE id = ?;

-- Contar grupos de un docente
SELECT COUNT(*) FROM grupos WHERE docenteId = ? AND activo = 1;
```

### Queries de Estudiante
```sql
-- Obtener estudiantes de un grupo
SELECT * FROM estudiantes WHERE grupoId = ? AND activo = 1 ORDER BY apellido, nombre;

-- Obtener estudiante por código
SELECT * FROM estudiantes WHERE codigo = ?;

-- Contar estudiantes en un grupo
SELECT COUNT(*) FROM estudiantes WHERE grupoId = ? AND activo = 1;
```

### Queries de Asistencia (MÁS IMPORTANTES)
```sql
-- Obtener registros de un grupo en una fecha específica
SELECT r.* FROM registros_asistencia r
INNER JOIN estudiantes e ON r.estudianteId = e.id
WHERE e.grupoId = ? AND r.fecha = ?;

-- Obtener registros de un estudiante
SELECT * FROM registros_asistencia WHERE estudianteId = ? ORDER BY fecha DESC;

-- Obtener registros en un rango de fechas
SELECT * FROM registros_asistencia
WHERE grupoId = ? AND fecha BETWEEN ? AND ?
ORDER BY fecha DESC;

-- Estadísticas de asistencia por grupo
SELECT
    estado,
    COUNT(*) as cantidad
FROM registros_asistencia
WHERE grupoId = ? AND fecha BETWEEN ? AND ?
GROUP BY estado;

-- Porcentaje de asistencia por estudiante
SELECT
    e.id,
    e.nombre,
    e.apellido,
    COUNT(CASE WHEN r.estado = 'PRESENTE' THEN 1 END) as presentes,
    COUNT(r.id) as total,
    (COUNT(CASE WHEN r.estado = 'PRESENTE' THEN 1 END) * 100.0 / COUNT(r.id)) as porcentaje
FROM estudiantes e
LEFT JOIN registros_asistencia r ON e.id = r.estudianteId
WHERE e.grupoId = ?
GROUP BY e.id;
```

---

## Migraciones

### Versión 1 (Actual)
- Schema inicial con todas las tablas
- No hay migraciones todavía

### Futuras Migraciones
Para cambiar el schema en futuras versiones:
1. Incrementar `version` en `@Database` annotation
2. Crear clase Migration
3. Actualizar schema exportado

**Ejemplo de migración:**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // SQL para modificar schema
        database.execSQL("ALTER TABLE estudiantes ADD COLUMN direccion TEXT")
    }
}
```

---

## Backup y Restore

### Exportar Base de Datos
```bash
# En dispositivo físico (requiere root o debug)
adb exec-out run-as sv.edu.catolica.asistedocente cat databases/asiste_docente_db > backup.db

# En emulador
adb pull /data/data/sv.edu.catolica.asistedocente/databases/asiste_docente_db backup.db
```

### Importar Base de Datos
```bash
# En emulador
adb push backup.db /data/data/sv.edu.catolica.asistedocente/databases/asiste_docente_db
```

---

## Inspección de la Base de Datos

### Android Studio Database Inspector
1. Abrir Android Studio
2. View → Tool Windows → App Inspection
3. Seleccionar tab "Database Inspector"
4. Conectar dispositivo/emulador
5. Seleccionar app y ver tablas en tiempo real

### SQLite Command Line
```bash
adb shell
run-as sv.edu.catolica.asistedocente
cd databases
sqlite3 asiste_docente_db

# Comandos útiles
.tables                    # Listar tablas
.schema docentes          # Ver schema de tabla
SELECT * FROM docentes;   # Query
.exit                     # Salir
```

---

## Consideraciones de Performance

### Índices Actuales
- Todos los campos `id` (Primary Keys)
- `docentes.email` (UNIQUE INDEX)
- `estudiantes.codigo` (UNIQUE INDEX)
- `grupos.docenteId` (INDEX)
- `estudiantes.grupoId` (INDEX)
- `registros_asistencia.estudianteId` (INDEX)
- `registros_asistencia.grupoId` (INDEX)
- `registros_asistencia.fecha` (INDEX)

### Optimizaciones Aplicadas
- Foreign Keys con ON DELETE CASCADE
- Índices en campos de búsqueda frecuente
- UNIQUE constraints para prevenir duplicados
- Queries con Flow para observación reactiva

### Futuras Optimizaciones (si es necesario)
- Índice compuesto en `registros_asistencia(grupoId, fecha)` si queries son lentas
- Paginación en listas con más de 100 elementos
- Archivado de datos antiguos (registros > 1 año)

---

## Notas de Seguridad

1. **Contraseñas**: Actualmente se almacenan como TEXT. En producción, se recomienda usar hashing (bcrypt, Argon2)
2. **Datos Sensibles**: Los datos de estudiantes son información sensible (FERPA/GDPR)
3. **Backup**: Implementar backup automático cifrado
4. **No hay API remota**: Toda la data es local, sin sincronización en la nube

---

## Referencias

- **Room Documentation**: https://developer.android.com/training/data-storage/room
- **Schema Export Location**: `app/schemas/sv.edu.catolica.asistedocente.data.local.database.AppDatabase/1.json`
- **Database Class**: `AppDatabase.kt` - Define todas las entities y versión
- **Type Converters**: `Converters.kt` - Conversión de tipos custom
