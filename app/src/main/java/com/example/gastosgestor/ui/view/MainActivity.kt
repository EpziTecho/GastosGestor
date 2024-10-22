package com.example.gastosgestor.ui.view

import AddProductFragment
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gastosgestor.R
import com.example.gastosgestor.databinding.ActivityMainBinding
import com.example.gastosgestor.ui.adapter.ProductAdapter
import com.example.gastosgestor.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    // View Binding
    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflar el layout usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar el RecyclerView
        productAdapter = ProductAdapter(emptyList())
        binding.recyclerViewProductos.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewProductos.adapter = productAdapter

        // Cargar los productos de Firestore
        obtenerProductosDeFirestore()

        // Al hacer clic en "AGREGAR PRODUCTO", navegar al AddProductFragment
        binding.btnAgregarProducto.setOnClickListener {
            mostrarAddProductFragment()
        }
    }

    // Función para mostrar el AddProductFragment y ocultar el RecyclerView y el botón
    private fun mostrarAddProductFragment() {
        // Ocultar RecyclerView y el botón para evitar solapamientos
        binding.recyclerViewProductos.visibility = View.GONE
        binding.btnAgregarProducto.visibility = View.GONE

        // Mostrar el fragmento AddProductFragment
        supportFragmentManager.commit {
            replace(R.id.fragment_container, AddProductFragment())
            addToBackStack(null) // Añadir a la pila para permitir regresar
        }
    }

    // Función para hacer visibles de nuevo los elementos del MainActivity
    fun mostrarElementosMainActivity() {
        binding.recyclerViewProductos.visibility = View.VISIBLE
        binding.btnAgregarProducto.visibility = View.VISIBLE
    }

    // Función para obtener los productos desde Firestore y actualizar el RecyclerView
    private fun obtenerProductosDeFirestore() {
        db.collection("productos").addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Error al escuchar los cambios: $e")
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val productos = snapshots.toObjects(Producto::class.java)
                productAdapter.setProductos(productos)  // Actualizar el RecyclerView con los productos
            }
        }
    }
}
