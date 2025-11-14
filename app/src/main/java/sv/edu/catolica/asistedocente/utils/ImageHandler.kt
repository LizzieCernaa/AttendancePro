package sv.edu.catolica.asistedocente.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Clase para manejar imágenes en la aplicación
 * Funciones: guardar, comprimir, crear URIs temporales
 */
class ImageHandler(private val context: Context) {

    companion object {
        private const val PHOTO_DIR = "photos"
        private const val TEMP_PHOTO_DIR = "temp_photos"
        private const val MAX_WIDTH = 1024
        private const val MAX_HEIGHT = 1024
        private const val COMPRESSION_QUALITY = 85
    }

    /**
     * Guarda una imagen desde un URI
     * Comprime la imagen si es necesaria
     * @return Ruta absoluta del archivo guardado
     */
    fun saveImage(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IOException("No se pudo abrir el archivo")

        // Decodificar la imagen
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        // Comprimir si es necesario
        val compressedBitmap = compressBitmapIfNeeded(originalBitmap)

        // Guardar en almacenamiento interno
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(getPhotoDirectory(), fileName)

        FileOutputStream(file).use { out ->
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, out)
        }

        // Limpiar recursos
        if (compressedBitmap != originalBitmap) {
            compressedBitmap.recycle()
        }
        originalBitmap.recycle()

        return file.absolutePath
    }

    /**
     * Comprime un bitmap si excede las dimensiones máximas
     */
    private fun compressBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Si la imagen ya es pequeña, no comprimir
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return bitmap
        }

        // Calcular nueva escala
        val ratio = minOf(
            MAX_WIDTH.toFloat() / width,
            MAX_HEIGHT.toFloat() / height
        )

        val newWidth = (width * ratio).roundToInt()
        val newHeight = (height * ratio).roundToInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    /**
     * Crea un archivo temporal para captura de foto con cámara
     * @return URI del archivo temporal
     */
    fun createTempImageUri(): Uri {
        val tempFile = File(getTempPhotoDirectory(), "temp_${System.currentTimeMillis()}.jpg")

        // Asegurarse de que el directorio existe
        tempFile.parentFile?.mkdirs()

        // Crear el archivo vacío
        tempFile.createNewFile()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    /**
     * Elimina una imagen del almacenamiento
     * @param imagePath Ruta completa de la imagen
     * @return true si se eliminó correctamente
     */
    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            file.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Limpia archivos temporales antiguos (más de 24 horas)
     */
    fun cleanTempFiles() {
        try {
            val tempDir = getTempPhotoDirectory()
            val now = System.currentTimeMillis()
            val dayInMillis = 24 * 60 * 60 * 1000 // 24 horas

            tempDir.listFiles()?.forEach { file ->
                if (now - file.lastModified() > dayInMillis) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Obtiene el directorio de fotos permanentes
     */
    private fun getPhotoDirectory(): File {
        val photoDir = File(context.filesDir, PHOTO_DIR)
        if (!photoDir.exists()) {
            photoDir.mkdirs()
        }
        return photoDir
    }

    /**
     * Obtiene el directorio de fotos temporales
     */
    private fun getTempPhotoDirectory(): File {
        val tempDir = File(context.cacheDir, TEMP_PHOTO_DIR)
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        return tempDir
    }

    /**
     * Verifica si un archivo de imagen existe
     */
    fun imageExists(imagePath: String?): Boolean {
        if (imagePath.isNullOrBlank()) return false
        val file = File(imagePath)
        return file.exists() && file.isFile
    }

    /**
     * Obtiene el tamaño de una imagen en bytes
     */
    fun getImageSize(imagePath: String): Long {
        return try {
            File(imagePath).length()
        } catch (e: Exception) {
            0L
        }
    }

    /**
     * Convierte bytes a formato legible (KB, MB)
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> String.format("%.2f KB", bytes / 1024.0)
            else -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
