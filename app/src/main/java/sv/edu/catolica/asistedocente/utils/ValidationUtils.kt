package sv.edu.catolica.asistedocente.utils

/**
 * Utilidades para validación de formularios
 * Contiene todas las reglas de validación de la aplicación
 */
object ValidationUtils {

    /**
     * Valida el nombre de un grupo
     */
    fun validateGroupName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("El nombre del grupo es requerido")
            name.length < 2 -> ValidationResult.Error("El nombre debe tener al menos 2 caracteres")
            name.length > 50 -> ValidationResult.Error("El nombre no puede exceder 50 caracteres")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida la materia de un grupo
     */
    fun validateSubject(subject: String): ValidationResult {
        return when {
            subject.isBlank() -> ValidationResult.Error("La materia es requerida")
            subject.length < 2 -> ValidationResult.Error("La materia debe tener al menos 2 caracteres")
            subject.length > 50 -> ValidationResult.Error("La materia no puede exceder 50 caracteres")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida el nombre de un estudiante
     */
    fun validateStudentName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Error("El nombre es requerido")
            name.length < 2 -> ValidationResult.Error("El nombre debe tener al menos 2 caracteres")
            name.length > 50 -> ValidationResult.Error("El nombre no puede exceder 50 caracteres")
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) ->
                ValidationResult.Error("El nombre solo debe contener letras")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida el apellido de un estudiante
     */
    fun validateStudentLastName(lastName: String): ValidationResult {
        return when {
            lastName.isBlank() -> ValidationResult.Error("El apellido es requerido")
            lastName.length < 2 -> ValidationResult.Error("El apellido debe tener al menos 2 caracteres")
            lastName.length > 50 -> ValidationResult.Error("El apellido no puede exceder 50 caracteres")
            !lastName.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) ->
                ValidationResult.Error("El apellido solo debe contener letras")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida el código de un estudiante
     */
    fun validateStudentCode(code: String): ValidationResult {
        return when {
            code.isBlank() -> ValidationResult.Error("El código es requerido")
            code.length < 3 -> ValidationResult.Error("El código debe tener al menos 3 caracteres")
            code.length > 20 -> ValidationResult.Error("El código no puede exceder 20 caracteres")
            !code.matches(Regex("^[a-zA-Z0-9-]+$")) ->
                ValidationResult.Error("El código debe ser alfanumérico")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida un email
     * @param email Email a validar
     * @param isRequired Si el email es requerido (false = opcional)
     */
    fun validateEmail(email: String, isRequired: Boolean = false): ValidationResult {
        if (email.isBlank()) {
            return if (isRequired) {
                ValidationResult.Error("El email es requerido")
            } else {
                ValidationResult.Success // Opcional y vacío es válido
            }
        }

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return if (email.matches(emailRegex)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("Email inválido")
        }
    }

    /**
     * Valida un número de teléfono (opcional)
     */
    fun validatePhone(phone: String): ValidationResult {
        if (phone.isBlank()) return ValidationResult.Success // Opcional

        return when {
            !phone.matches(Regex("^[0-9-]+$")) ->
                ValidationResult.Error("El teléfono solo debe contener números y guiones")
            phone.replace("-", "").length < 8 ->
                ValidationResult.Error("El teléfono debe tener al menos 8 dígitos")
            phone.replace("-", "").length > 15 ->
                ValidationResult.Error("El teléfono no puede exceder 15 dígitos")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida el horario de un grupo (opcional)
     */
    fun validateSchedule(schedule: String): ValidationResult {
        // El horario es opcional, así que si está vacío es válido
        if (schedule.isBlank()) return ValidationResult.Success

        return when {
            schedule.length > 100 -> ValidationResult.Error("El horario no puede exceder 100 caracteres")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida la descripción de un grupo (opcional)
     */
    fun validateDescription(description: String): ValidationResult {
        // La descripción es opcional
        if (description.isBlank()) return ValidationResult.Success

        return when {
            description.length > 500 -> ValidationResult.Error("La descripción no puede exceder 500 caracteres")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida notas de asistencia (opcional)
     */
    fun validateNotes(notes: String): ValidationResult {
        // Las notas son opcionales
        if (notes.isBlank()) return ValidationResult.Success

        return when {
            notes.length > 200 -> ValidationResult.Error("Las notas no pueden exceder 200 caracteres")
            else -> ValidationResult.Success
        }
    }

    /**
     * Valida que un campo no esté vacío (genérico)
     */
    fun validateRequired(value: String, fieldName: String): ValidationResult {
        return if (value.isBlank()) {
            ValidationResult.Error("$fieldName es requerido")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida longitud mínima
     */
    fun validateMinLength(value: String, minLength: Int, fieldName: String): ValidationResult {
        return if (value.length < minLength) {
            ValidationResult.Error("$fieldName debe tener al menos $minLength caracteres")
        } else {
            ValidationResult.Success
        }
    }

    /**
     * Valida longitud máxima
     */
    fun validateMaxLength(value: String, maxLength: Int, fieldName: String): ValidationResult {
        return if (value.length > maxLength) {
            ValidationResult.Error("$fieldName no puede exceder $maxLength caracteres")
        } else {
            ValidationResult.Success
        }
    }
}

/**
 * Resultado de una validación
 */
sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    fun isValid(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun errorMessage(): String? = (this as? Error)?.message
}
