package com.example.ventasgestor.csv

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class CsvUploader {

    fun subirCsvAFirebaseStorage(context: Context) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("informes_productos/productos.csv")

        // Ruta del archivo CSV
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
        val file = File(storageDir, "informes_productos/productos.csv")

        val fileUri = Uri.fromFile(file)
        storageRef.putFile(fileUri)
            .addOnSuccessListener {
                println("Archivo CSV subido exitosamente a Firebase Storage")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                println("Error al subir el archivo CSV a Firebase Storage")
            }
    }
}
