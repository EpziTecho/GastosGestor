package com.example.gastosgestor.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gastosgestor.data.model.Producto
import com.example.gastosgestor.data.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    // LiveData para notificar si el producto fue agregado exitosamente o no
    private val _agregarExitoso = MutableLiveData<Boolean>()
    val agregarExitoso: LiveData<Boolean> get() = _agregarExitoso

    // Método para agregar un producto
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            try {
                productRepository.agregarProducto(producto)
                _agregarExitoso.postValue(true)  // Notificar éxito
            } catch (e: Exception) {
                _agregarExitoso.postValue(false)  // Notificar error
            }
        }
    }
}
