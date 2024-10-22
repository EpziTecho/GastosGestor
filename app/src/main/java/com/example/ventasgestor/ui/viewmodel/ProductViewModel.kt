package com.example.ventasgestor.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ventasgestor.data.model.Producto
import com.example.ventasgestor.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    // LiveData para notificar si el producto fue agregado exitosamente o no
    private val _agregarExitoso = MutableLiveData<Boolean>()
    val agregarExitoso: LiveData<Boolean> get() = _agregarExitoso

    // Método para agregar un producto con imagen
    fun agregarProducto(producto: Producto, uriImagen: Uri?) {
        viewModelScope.launch {
            try {
                // Si hay una imagen, primero subirla a Firebase Storage
                val urlImagen = uriImagen?.let {
                    productRepository.subirImagenProducto(it) // Subir la imagen y obtener la URL
                } ?: ""

                // Actualizar el producto con la URL de la imagen
                val productoConImagen = producto.copy(fotoUrl = urlImagen)

                // Luego, agregar el producto a Firestore
                productRepository.agregarProducto(productoConImagen)

                // Notificar éxito
                _agregarExitoso.postValue(true)
            } catch (e: Exception) {
                // Notificar error
                _agregarExitoso.postValue(false)
            }
        }
    }
}
