package sv.edu.catolica.asistedocente.ui.screens.profile

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import sv.edu.catolica.asistedocente.data.local.entities.Docente
import sv.edu.catolica.asistedocente.data.repository.DocenteRepository
import sv.edu.catolica.asistedocente.utils.ImageHandler
import sv.edu.catolica.asistedocente.utils.ValidationUtils
import sv.edu.catolica.asistedocente.utils.ValidationResult
import javax.inject.Inject

/**
 * ViewModel para la pantalla de perfil de docente
 */
@HiltViewModel
class DocenteProfileViewModel @Inject constructor(
    application: Application,
    private val docenteRepository: DocenteRepository
) : AndroidViewModel(application) {

    private val context = application.applicationContext
    private val imageHandler = ImageHandler(context)

    private val _uiState = MutableStateFlow(DocenteProfileUiState())
    val uiState: StateFlow<DocenteProfileUiState> = _uiState.asStateFlow()

    private var currentDocenteId: Long? = null

    init {
        loadProfile()
    }

    /**
     * Carga el perfil del docente actual
     */
    private fun loadProfile() {
        viewModelScope.launch {
            try {
                // Obtener el primer docente activo
                val docente = docenteRepository.getAllDocentes()
                    .firstOrNull()
                    ?.firstOrNull { it.activo }

                if (docente != null) {
                    currentDocenteId = docente.id
                    _uiState.update {
                        it.copy(
                            nombre = docente.nombre,
                            apellido = docente.apellido,
                            email = docente.email ?: "",
                            telefono = docente.telefono ?: "",
                            photoPath = docente.foto
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Actualiza el nombre
     */
    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, nombreError = null) }
    }

    /**
     * Actualiza el apellido
     */
    fun onApellidoChange(apellido: String) {
        _uiState.update { it.copy(apellido = apellido, apellidoError = null) }
    }

    /**
     * Actualiza el email
     */
    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    /**
     * Actualiza el teléfono
     */
    fun onTelefonoChange(telefono: String) {
        _uiState.update { it.copy(telefono = telefono, telefonoError = null) }
    }

    /**
     * Actualiza la contraseña actual
     */
    fun onCurrentPasswordChange(password: String) {
        _uiState.update { it.copy(currentPassword = password, currentPasswordError = null) }
    }

    /**
     * Actualiza la nueva contraseña
     */
    fun onNewPasswordChange(password: String) {
        _uiState.update { it.copy(newPassword = password, newPasswordError = null) }
    }

    /**
     * Actualiza la confirmación de nueva contraseña
     */
    fun onConfirmNewPasswordChange(password: String) {
        _uiState.update { it.copy(confirmNewPassword = password, confirmNewPasswordError = null) }
    }

    /**
     * Prepara la captura de foto
     */
    fun onTakePhotoClick() {
        viewModelScope.launch {
            try {
                val tempUri = imageHandler.createTempImageUri()
                _uiState.update { it.copy(tempPhotoUri = tempUri) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Maneja la foto capturada desde la cámara
     */
    fun onPhotoCaptured() {
        viewModelScope.launch {
            try {
                val tempUri = _uiState.value.tempPhotoUri
                if (tempUri != null) {
                    val savedPath = imageHandler.saveImage(tempUri)

                    // Eliminar foto anterior si existe
                    _uiState.value.photoPath?.let { oldPath ->
                        imageHandler.deleteImage(oldPath)
                    }

                    _uiState.update {
                        it.copy(
                            photoPath = savedPath,
                            tempPhotoUri = null
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(tempPhotoUri = null) }
            }
        }
    }

    /**
     * Maneja la foto seleccionada desde galería
     */
    fun onPhotoSelected(uri: Uri) {
        viewModelScope.launch {
            try {
                val savedPath = imageHandler.saveImage(uri)

                // Eliminar foto anterior si existe
                _uiState.value.photoPath?.let { oldPath ->
                    imageHandler.deleteImage(oldPath)
                }

                _uiState.update { it.copy(photoPath = savedPath) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Elimina la foto actual
     */
    fun removePhoto() {
        viewModelScope.launch {
            _uiState.value.photoPath?.let { path ->
                imageHandler.deleteImage(path)
            }
            _uiState.update { it.copy(photoPath = null) }
        }
    }

    /**
     * Valida y guarda el perfil
     */
    fun saveProfile() {
        viewModelScope.launch {
            // Limpiar errores previos
            _uiState.update {
                it.copy(
                    nombreError = null,
                    apellidoError = null,
                    emailError = null,
                    telefonoError = null,
                    currentPasswordError = null,
                    newPasswordError = null,
                    confirmNewPasswordError = null
                )
            }

            val state = _uiState.value
            var hasErrors = false

            // Validar nombre
            when (val nombreValidation = ValidationUtils.validateStudentName(state.nombre)) {
                is ValidationResult.Success -> { }
                is ValidationResult.Error -> {
                    _uiState.update { it.copy(nombreError = nombreValidation.message) }
                    hasErrors = true
                }
            }

            // Validar apellido
            when (val apellidoValidation = ValidationUtils.validateStudentLastName(state.apellido)) {
                is ValidationResult.Success -> { }
                is ValidationResult.Error -> {
                    _uiState.update { it.copy(apellidoError = apellidoValidation.message) }
                    hasErrors = true
                }
            }

            // Validar email (opcional pero si está presente debe ser válido)
            if (state.email.isNotBlank()) {
                when (val emailValidation = ValidationUtils.validateEmail(state.email, isRequired = false)) {
                    is ValidationResult.Success -> { }
                    is ValidationResult.Error -> {
                        _uiState.update { it.copy(emailError = emailValidation.message) }
                        hasErrors = true
                    }
                }
            }

            // Validar teléfono (opcional)
            if (state.telefono.isNotBlank()) {
                when (val telefonoValidation = ValidationUtils.validatePhone(state.telefono)) {
                    is ValidationResult.Success -> { }
                    is ValidationResult.Error -> {
                        _uiState.update { it.copy(telefonoError = telefonoValidation.message) }
                        hasErrors = true
                    }
                }
            }

            // Validar cambio de contraseña (solo si se proporcionan los campos)
            val isChangingPassword = state.currentPassword.isNotBlank() ||
                                    state.newPassword.isNotBlank() ||
                                    state.confirmNewPassword.isNotBlank()

            var newPassword: String? = null
            if (isChangingPassword) {
                if (state.currentPassword.isBlank()) {
                    _uiState.update { it.copy(currentPasswordError = "Ingrese contraseña actual") }
                    hasErrors = true
                } else if (currentDocenteId != null) {
                    // Verificar que la contraseña actual sea correcta
                    val docente = docenteRepository.getAllDocentes().firstOrNull()?.find { it.id == currentDocenteId }
                    if (docente != null && docente.password != state.currentPassword) {
                        _uiState.update { it.copy(currentPasswordError = "Contraseña actual incorrecta") }
                        hasErrors = true
                    }
                }

                if (state.newPassword.isBlank()) {
                    _uiState.update { it.copy(newPasswordError = "Ingrese nueva contraseña") }
                    hasErrors = true
                } else if (state.newPassword.length < 4) {
                    _uiState.update { it.copy(newPasswordError = "Mínimo 4 caracteres") }
                    hasErrors = true
                }

                if (state.confirmNewPassword.isBlank()) {
                    _uiState.update { it.copy(confirmNewPasswordError = "Confirme nueva contraseña") }
                    hasErrors = true
                } else if (state.newPassword != state.confirmNewPassword) {
                    _uiState.update { it.copy(confirmNewPasswordError = "Las contraseñas no coinciden") }
                    hasErrors = true
                }

                if (!hasErrors) {
                    newPassword = state.newPassword
                }
            }

            if (hasErrors) {
                return@launch
            }

            // Guardar perfil
            _uiState.update { it.copy(isSaving = true) }

            try {
                // Obtener docente actual para preservar la contraseña si no se cambia
                val currentDocente = if (currentDocenteId != null) {
                    docenteRepository.getAllDocentes().firstOrNull()?.find { it.id == currentDocenteId }
                } else null

                val docente = if (currentDocenteId != null) {
                    // Actualizar docente existente
                    Docente(
                        id = currentDocenteId!!,
                        nombre = state.nombre,
                        apellido = state.apellido,
                        email = state.email,
                        password = newPassword ?: currentDocente?.password ?: "",
                        telefono = state.telefono.takeIf { it.isNotBlank() },
                        foto = state.photoPath,
                        activo = true,
                        fechaCreacion = currentDocente?.fechaCreacion ?: System.currentTimeMillis()
                    )
                } else {
                    // Crear nuevo docente
                    Docente(
                        nombre = state.nombre,
                        apellido = state.apellido,
                        email = state.email,
                        password = newPassword ?: "1234",
                        telefono = state.telefono.takeIf { it.isNotBlank() },
                        foto = state.photoPath,
                        activo = true,
                        fechaCreacion = System.currentTimeMillis()
                    )
                }

                if (currentDocenteId != null) {
                    docenteRepository.updateDocente(docente)
                } else {
                    val newId = docenteRepository.insertDocente(docente)
                    currentDocenteId = newId
                }

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccess = true,
                        currentPassword = "",
                        newPassword = "",
                        confirmNewPassword = ""
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}

/**
 * Estado de la UI del perfil de docente
 */
data class DocenteProfileUiState(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val photoPath: String? = null,
    val tempPhotoUri: Uri? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmNewPassword: String = "",
    val nombreError: String? = null,
    val apellidoError: String? = null,
    val emailError: String? = null,
    val telefonoError: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmNewPasswordError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
)
