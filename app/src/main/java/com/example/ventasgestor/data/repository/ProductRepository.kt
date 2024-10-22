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

    // Método para obtener todos los productos
    suspend fun obtenerProductos(): List<Producto> {
        return try {
            val snapshot = coleccionProductos.get().await()
            snapshot.toObjects(Producto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Método para obtener un producto por su id
    suspend fun obtenerProductoPorId(id: String): Producto? {
        return try {
            val snapshot = coleccionProductos.document(id).get().await()
            snapshot.toObject(Producto::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Método para subir una foto a Firebase Storage
    suspend fun subirImagenProducto(idProducto: String, uriImagen: String): String? {
        return try {
            val referenciaAlmacenamiento = almacenamiento.reference.child("imagenes_productos/$idProducto.jpg")
            referenciaAlmacenamiento.putFile(Uri.parse(uriImagen)).await()
            referenciaAlmacenamiento.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Método para eliminar un producto
    suspend fun eliminarProducto(id: String): Boolean {
        return try {
            coleccionProductos.document(id).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
