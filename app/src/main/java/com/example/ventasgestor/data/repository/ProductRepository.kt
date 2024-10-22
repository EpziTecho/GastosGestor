package com.example.ventasgestor.data.repository

import android.net.Uri
import com.example.ventasgestor.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val baseDatos = FirebaseFirestore.getInstance()
    private val almacenamiento = FirebaseStorage.getInstance()

    // Referencia a la colección de productos
    private val coleccionProductos = baseDatos.collection("productos")

    // Método para agregar un producto
    suspend fun agregarProducto(producto: Producto): Boolean {
        return try {
            val nuevoDocumento = coleccionProductos.document() // Generar un ID único de Firebase
            producto.id = nuevoDocumento.id  // Asignar el ID generado al producto
            nuevoDocumento.set(producto).await()  // Guardar el producto en Firebase
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // Método para subir una imagen a Firebase Storage
    suspend fun subirImagenProducto(uriImagen: Uri): String? {
        return try {
            // Crear una referencia de almacenamiento con un nombre único para la imagen
            val referenciaAlmacenamiento = almacenamiento.reference.child("imagenes/${System.currentTimeMillis()}.jpg")

            // Subir el archivo al almacenamiento
            referenciaAlmacenamiento.putFile(uriImagen).await()

            // Obtener la URL de descarga de la imagen subida
            val urlImagen = referenciaAlmacenamiento.downloadUrl.await().toString()

            // Devolver la URL de la imagen
            urlImagen
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


