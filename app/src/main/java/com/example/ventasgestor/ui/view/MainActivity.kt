package com.example.ventasgestor.ui.view

import AddProductFragment
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ventasgestor.R
import com.example.ventasgestor.data.model.Producto
import com.example.ventasgestor.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import androidx.fragment.app.commit
import com.example.ventasgestor.adapter.ProductAdapter
import java.text.SimpleDateFormat
import java.util.*

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

        // Mostrar el mes actual y convertirlo a Español
        val currentMonth = obtenerMesActual()
        binding.tvMes.text = currentMonth


        // Configurar el RecyclerView
        configurarRecyclerView()

        // Cargar los productos de Firestore
        obtenerProductosDeFirestore()

        // Al hacer clic en "AGREGAR PRODUCTO", navegar al AddProductFragment
        binding.btnAgregarProducto.setOnClickListener {
            mostrarAddProductFragment()
        }
    }

    // Función para configurar el RecyclerView
    private fun configurarRecyclerView() {
        productAdapter = ProductAdapter(emptyList())
        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = productAdapter
        }
    }

    // Función para mostrar el AddProductFragment y ocultar el RecyclerView y otros elementos
    private fun mostrarAddProductFragment() {
        // Ocultar elementos
        binding.recyclerViewProductos.visibility = View.GONE // Ocultar el RecyclerView
        binding.btnAgregarProducto.visibility = View.GONE // Ocultar el botón de agregar producto
        binding.textView.visibility = View.GONE // Ocultar el título
        binding.tvMes.visibility = View.GONE // Ocultar el TextView del mes
        binding.tvMontoTotal.visibility = View.GONE // Ocultar el TextView del monto total

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
        binding.textView.visibility = View.VISIBLE
        binding.tvMes.visibility = View.VISIBLE
        binding.tvMontoTotal.visibility = View.VISIBLE
    }

    // Función para obtener los productos desde Firestore y actualizar el RecyclerView y el monto total
    private fun obtenerProductosDeFirestore() {
        db.collection("productos").addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Error al escuchar los cambios: $e")
                return@addSnapshotListener
            }

            if (snapshots != null && !snapshots.isEmpty) {
                val productos = snapshots.toObjects(Producto::class.java)
                productAdapter.setProductos(productos)  // Actualizar el RecyclerView con los productos

                // Calcular el monto total
                val montoTotal = productos.sumOf { it.montoFinal }
                binding.tvMontoTotal.text = "Monto Total: S/. $montoTotal"
            }
        }
    }

    // Función para obtener el mes actual
    private fun obtenerMesActual(): String {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))
        return sdf.format(Date())
    }
}
