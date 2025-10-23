package com.tienda.tienda_virtual.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    
    /**
     * Copia una imagen desde un URI temporal a almacenamiento interno de la app
     * y retorna la ruta del archivo guardado
     */
    fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            
            // Crear directorio de imágenes si no existe
            val imagesDir = File(context.filesDir, "product_images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }
            
            // Generar nombre único para la imagen
            val fileName = "img_${UUID.randomUUID()}.jpg"
            val imageFile = File(imagesDir, fileName)
            
            // Copiar la imagen al almacenamiento interno
            FileOutputStream(imageFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            
            inputStream.close()
            
            // Retornar la ruta absoluta del archivo
            imageFile.absolutePath
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error al guardar imagen: ${e.message}", e)
            null
        }
    }
    
    /**
     * Elimina una imagen del almacenamiento interno
     */
    fun deleteImageFromInternalStorage(imagePath: String?): Boolean {
        return try {
            if (imagePath.isNullOrBlank()) return false
            
            val file = File(imagePath)
            if (file.exists()) {
                file.delete()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("ImageUtils", "Error al eliminar imagen: ${e.message}", e)
            false
        }
    }
    
    /**
     * Verifica si una ruta es una URI de contenido temporal
     */
    fun isContentUri(path: String?): Boolean {
        return path?.startsWith("content://") == true
    }
    
    /**
     * Verifica si una ruta es una URL de internet
     */
    fun isHttpUrl(path: String?): Boolean {
        return path?.startsWith("http://") == true || path?.startsWith("https://") == true
    }
    
    /**
     * Obtiene el URI de un archivo de almacenamiento interno
     */
    fun getUriFromPath(path: String?): Uri? {
        return if (path.isNullOrBlank()) {
            null
        } else if (isContentUri(path) || isHttpUrl(path)) {
            Uri.parse(path)
        } else {
            Uri.fromFile(File(path))
        }
    }
}

