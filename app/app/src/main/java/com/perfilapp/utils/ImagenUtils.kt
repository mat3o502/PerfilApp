package com.perfilapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

object ImagenUtils {

    // Carga la foto reduciendo su tamaño (para no gastar memoria) y corrigiendo su rotación
    fun cargarBitmap(ruta: String, maxLado: Int = 1024): Bitmap? {
        val medidas = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(ruta, medidas)

        var reduccion = 1
        while (medidas.outWidth / reduccion > maxLado * 2 || medidas.outHeight / reduccion > maxLado * 2) {
            reduccion *= 2
        }

        val opciones = BitmapFactory.Options().apply { inSampleSize = reduccion }
        val bitmap = BitmapFactory.decodeFile(ruta, opciones) ?: return null

        val orientacion = try {
            ExifInterface(ruta).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }

        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (grados == 0f) return bitmap

        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }
}