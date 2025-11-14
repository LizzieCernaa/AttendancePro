-- ========================================
-- Base de Datos: asiste_docente_db
-- Versión: 1
-- Generado por: Room Database Schema
-- ========================================

-- Tabla: docentes
-- Almacena información de los docentes
CREATE TABLE IF NOT EXISTS `docentes` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `nombre` TEXT NOT NULL,
    `apellido` TEXT NOT NULL,
    `email` TEXT NOT NULL,
    `telefono` TEXT,
    `foto` TEXT,
    `activo` INTEGER NOT NULL,
    `fechaCreacion` INTEGER NOT NULL
);

-- Tabla: grupos
-- Almacena información de los grupos/clases
CREATE TABLE IF NOT EXISTS `grupos` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `nombre` TEXT NOT NULL,
    `materia` TEXT NOT NULL,
    `horario` TEXT,
    `docenteId` INTEGER NOT NULL,
    `activo` INTEGER NOT NULL,
    `fechaCreacion` INTEGER NOT NULL,
    `descripcion` TEXT,
    FOREIGN KEY(`docenteId`) REFERENCES `docentes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Índice para optimizar consultas por docenteId
CREATE INDEX IF NOT EXISTS `index_grupos_docenteId` ON `grupos` (`docenteId`);

-- Tabla: estudiantes
-- Almacena información de los estudiantes
CREATE TABLE IF NOT EXISTS `estudiantes` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `nombre` TEXT NOT NULL,
    `apellido` TEXT NOT NULL,
    `codigo` TEXT NOT NULL,
    `email` TEXT,
    `foto` TEXT,
    `grupoId` INTEGER NOT NULL,
    `activo` INTEGER NOT NULL,
    `fechaCreacion` INTEGER NOT NULL,
    FOREIGN KEY(`grupoId`) REFERENCES `grupos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Índice para optimizar consultas por grupoId
CREATE INDEX IF NOT EXISTS `index_estudiantes_grupoId` ON `estudiantes` (`grupoId`);

-- Índice único para código de estudiante (evita duplicados)
CREATE UNIQUE INDEX IF NOT EXISTS `index_estudiantes_codigo` ON `estudiantes` (`codigo`);

-- Tabla: registros_asistencia
-- Almacena los registros de asistencia de cada estudiante
CREATE TABLE IF NOT EXISTS `registros_asistencia` (
    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `estudianteId` INTEGER NOT NULL,
    `grupoId` INTEGER NOT NULL,
    `fecha` INTEGER NOT NULL,
    `estado` TEXT NOT NULL,
    `notas` TEXT,
    `horaRegistro` INTEGER NOT NULL,
    FOREIGN KEY(`estudianteId`) REFERENCES `estudiantes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
    FOREIGN KEY(`grupoId`) REFERENCES `grupos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

-- Índices para optimizar consultas de asistencia
CREATE INDEX IF NOT EXISTS `index_registros_asistencia_estudianteId` ON `registros_asistencia` (`estudianteId`);
CREATE INDEX IF NOT EXISTS `index_registros_asistencia_grupoId` ON `registros_asistencia` (`grupoId`);
CREATE INDEX IF NOT EXISTS `index_registros_asistencia_fecha` ON `registros_asistencia` (`fecha`);

-- Índice único compuesto para evitar duplicados (un estudiante solo puede tener un registro por día)
CREATE UNIQUE INDEX IF NOT EXISTS `index_registros_asistencia_estudianteId_fecha` ON `registros_asistencia` (`estudianteId`, `fecha`);

-- Tabla interna de Room (no modificar)
CREATE TABLE IF NOT EXISTS room_master_table (
    id INTEGER PRIMARY KEY,
    identity_hash TEXT
);

INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES(42, 'ceab0edaf394fc7897e833f61c93f679');

-- ========================================
-- NOTAS IMPORTANTES:
-- ========================================
-- 1. Los campos INTEGER con valor booleano (activo) usan: 0 = false, 1 = true
-- 2. Los campos de fecha (fechaCreacion, fecha, horaRegistro) son timestamps en milisegundos (Long)
-- 3. El campo 'estado' en registros_asistencia acepta: PRESENTE, AUSENTE, TARDANZA, JUSTIFICADO
-- 4. Las Foreign Keys tienen ON DELETE CASCADE, eliminando registros dependientes automáticamente
-- 5. El código de estudiante es ÚNICO en toda la base de datos
-- 6. Un estudiante no puede tener dos registros de asistencia en la misma fecha

-- ========================================
-- DATOS DE PRUEBA (Opcional - Comentado)
-- ========================================

-- Insertar un docente de prueba
-- INSERT INTO docentes (nombre, apellido, email, telefono, activo, fechaCreacion)
-- VALUES ('Juan', 'Pérez', 'juan.perez@catolica.edu.sv', '7777-7777', 1, strftime('%s', 'now') * 1000);

-- Insertar un grupo de prueba (asume docenteId = 1)
-- INSERT INTO grupos (nombre, materia, horario, docenteId, activo, fechaCreacion, descripcion)
-- VALUES ('Grupo A', 'Programación I', 'Lunes 8:00 AM', 1, 1, strftime('%s', 'now') * 1000, 'Grupo de mañana');

-- Insertar estudiantes de prueba (asume grupoId = 1)
-- INSERT INTO estudiantes (nombre, apellido, codigo, email, grupoId, activo, fechaCreacion)
-- VALUES
--     ('María', 'García', 'EST001', 'maria.garcia@catolica.edu.sv', 1, 1, strftime('%s', 'now') * 1000),
--     ('Carlos', 'López', 'EST002', 'carlos.lopez@catolica.edu.sv', 1, 1, strftime('%s', 'now') * 1000),
--     ('Ana', 'Martínez', 'EST003', 'ana.martinez@catolica.edu.sv', 1, 1, strftime('%s', 'now') * 1000);

-- Insertar registros de asistencia de prueba
-- INSERT INTO registros_asistencia (estudianteId, grupoId, fecha, estado, horaRegistro)
-- VALUES
--     (1, 1, strftime('%s', 'now') * 1000, 'PRESENTE', strftime('%s', 'now') * 1000),
--     (2, 1, strftime('%s', 'now') * 1000, 'TARDANZA', strftime('%s', 'now') * 1000),
--     (3, 1, strftime('%s', 'now') * 1000, 'AUSENTE', strftime('%s', 'now') * 1000);
