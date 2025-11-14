package sv.edu.catolica.asistedocente.utils

import sv.edu.catolica.asistedocente.data.local.entities.Docente
import sv.edu.catolica.asistedocente.data.local.entities.Estudiante
import sv.edu.catolica.asistedocente.data.local.entities.Grupo
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.data.repository.EstudianteRepository
import sv.edu.catolica.asistedocente.data.repository.GrupoRepository

/**
 * Genera datos de ejemplo para la aplicación
 */
object SampleDataGenerator {

    /**
     * Genera y guarda datos de ejemplo en la base de datos
     */
    suspend fun generateSampleData(
        docenteRepository: DocenteRepository,
        grupoRepository: GrupoRepository,
        estudianteRepository: EstudianteRepository
    ) {
        // Crear docente de ejemplo
        val docenteId = docenteRepository.insertDocente(
            Docente(
                nombre = "María",
                apellido = "González",
                email = "maria.gonzalez@catolica.edu.sv",
                telefono = "7890-1234",
                activo = true
            )
        )

        // Crear grupos de ejemplo
        val grupos = listOf(
            Grupo(
                nombre = "Matemáticas I",
                materia = "Matemáticas",
                horario = "Lunes y Miércoles 8:00-10:00",
                descripcion = "Álgebra y geometría básica",
                docenteId = docenteId,
                activo = true
            ),
            Grupo(
                nombre = "Programación Java",
                materia = "Programación",
                horario = "Martes y Jueves 10:00-12:00",
                descripcion = "Fundamentos de Java y POO",
                docenteId = docenteId,
                activo = true
            ),
            Grupo(
                nombre = "Base de Datos",
                materia = "Informática",
                horario = "Viernes 14:00-18:00",
                descripcion = "SQL, diseño y normalización",
                docenteId = docenteId,
                activo = true
            ),
            Grupo(
                nombre = "Inglés Técnico",
                materia = "Idiomas",
                horario = "Lunes 14:00-16:00",
                descripcion = "Inglés para carreras técnicas",
                docenteId = docenteId,
                activo = true
            )
        )

        val grupoIds = grupos.map { grupoRepository.insertGrupo(it) }

        // Estudiantes para Matemáticas I
        val estudiantesMatematicas = listOf(
            Estudiante(
                nombre = "Juan",
                apellido = "Pérez",
                codigo = "2024-0001",
                email = "juan.perez@catolica.edu.sv",
                grupoId = grupoIds[0]
            ),
            Estudiante(
                nombre = "Ana",
                apellido = "Martínez",
                codigo = "2024-0002",
                email = "ana.martinez@catolica.edu.sv",
                grupoId = grupoIds[0]
            ),
            Estudiante(
                nombre = "Carlos",
                apellido = "López",
                codigo = "2024-0003",
                email = "carlos.lopez@catolica.edu.sv",
                grupoId = grupoIds[0]
            ),
            Estudiante(
                nombre = "Laura",
                apellido = "Hernández",
                codigo = "2024-0004",
                email = "laura.hernandez@catolica.edu.sv",
                grupoId = grupoIds[0]
            ),
            Estudiante(
                nombre = "Miguel",
                apellido = "García",
                codigo = "2024-0005",
                email = "miguel.garcia@catolica.edu.sv",
                grupoId = grupoIds[0]
            )
        )

        // Estudiantes para Programación Java
        val estudiantesProgramacion = listOf(
            Estudiante(
                nombre = "Sofia",
                apellido = "Ramírez",
                codigo = "2024-0006",
                email = "sofia.ramirez@catolica.edu.sv",
                grupoId = grupoIds[1]
            ),
            Estudiante(
                nombre = "Diego",
                apellido = "Torres",
                codigo = "2024-0007",
                email = "diego.torres@catolica.edu.sv",
                grupoId = grupoIds[1]
            ),
            Estudiante(
                nombre = "Valeria",
                apellido = "Flores",
                codigo = "2024-0008",
                email = "valeria.flores@catolica.edu.sv",
                grupoId = grupoIds[1]
            ),
            Estudiante(
                nombre = "Roberto",
                apellido = "Morales",
                codigo = "2024-0009",
                email = "roberto.morales@catolica.edu.sv",
                grupoId = grupoIds[1]
            ),
            Estudiante(
                nombre = "Patricia",
                apellido = "Castro",
                codigo = "2024-0010",
                email = "patricia.castro@catolica.edu.sv",
                grupoId = grupoIds[1]
            ),
            Estudiante(
                nombre = "Fernando",
                apellido = "Ruiz",
                codigo = "2024-0011",
                email = "fernando.ruiz@catolica.edu.sv",
                grupoId = grupoIds[1]
            )
        )

        // Estudiantes para Base de Datos
        val estudiantesBD = listOf(
            Estudiante(
                nombre = "Carmen",
                apellido = "Jiménez",
                codigo = "2024-0012",
                email = "carmen.jimenez@catolica.edu.sv",
                grupoId = grupoIds[2]
            ),
            Estudiante(
                nombre = "Ricardo",
                apellido = "Vargas",
                codigo = "2024-0013",
                email = "ricardo.vargas@catolica.edu.sv",
                grupoId = grupoIds[2]
            ),
            Estudiante(
                nombre = "Gabriela",
                apellido = "Mendoza",
                codigo = "2024-0014",
                email = "gabriela.mendoza@catolica.edu.sv",
                grupoId = grupoIds[2]
            ),
            Estudiante(
                nombre = "Andrés",
                apellido = "Silva",
                codigo = "2024-0015",
                email = "andres.silva@catolica.edu.sv",
                grupoId = grupoIds[2]
            )
        )

        // Estudiantes para Inglés Técnico
        val estudiantesIngles = listOf(
            Estudiante(
                nombre = "Monica",
                apellido = "Reyes",
                codigo = "2024-0016",
                email = "monica.reyes@catolica.edu.sv",
                grupoId = grupoIds[3]
            ),
            Estudiante(
                nombre = "Javier",
                apellido = "Ortiz",
                codigo = "2024-0017",
                email = "javier.ortiz@catolica.edu.sv",
                grupoId = grupoIds[3]
            ),
            Estudiante(
                nombre = "Daniela",
                apellido = "Guzmán",
                codigo = "2024-0018",
                email = "daniela.guzman@catolica.edu.sv",
                grupoId = grupoIds[3]
            ),
            Estudiante(
                nombre = "Sergio",
                apellido = "Medina",
                codigo = "2024-0019",
                email = "sergio.medina@catolica.edu.sv",
                grupoId = grupoIds[3]
            ),
            Estudiante(
                nombre = "Elena",
                apellido = "Paredes",
                codigo = "2024-0020",
                email = "elena.paredes@catolica.edu.sv",
                grupoId = grupoIds[3]
            )
        )

        // Insertar todos los estudiantes
        (estudiantesMatematicas + estudiantesProgramacion + estudiantesBD + estudiantesIngles).forEach {
            estudianteRepository.insertEstudiante(it)
        }
    }
}
