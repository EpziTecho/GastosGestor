import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.gastosgestor.csv.CsvGenerator
import com.example.gastosgestor.data.model.Producto
import com.example.gastosgestor.databinding.FragmentAddProductBinding
import com.example.gastosgestor.ui.viewmodel.ProductViewModel
import com.example.gastosgestor.ui.viewmodel.ProductViewModelFactory
import com.example.gastosgestor.data.repository.ProductRepository
import androidx.lifecycle.ViewModelProvider
import com.example.gastosgestor.ui.view.MainActivity

class AddProductFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Crear la instancia de ProductRepository
        val productRepository = ProductRepository()

        // Crear el ViewModel usando el ProductViewModelFactory
        val factory = ProductViewModelFactory(productRepository)
        productViewModel = ViewModelProvider(this, factory).get(ProductViewModel::class.java)

        // Observar el resultado de agregar el producto
        productViewModel.agregarExitoso.observe(viewLifecycleOwner, { exito ->
            if (exito) {
                Toast.makeText(context, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
                limpiarCampos()  // Limpiar los campos
            } else {
                Toast.makeText(context, "Error al agregar el producto", Toast.LENGTH_SHORT).show()
            }
        })
        // Configurar el botón "REGRESAR" para volver al MainActivity
        binding.btnRegresar.setOnClickListener {
            // Hacer que el RecyclerView y el botón de agregar producto vuelvan a ser visibles
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as MainActivity).mostrarElementosMainActivity() // Mostrar RecyclerView y botón en MainActivity
        }
        // Agregar el producto cuando se haga clic en el botón
        binding.btnAgregarProducto.setOnClickListener {
            agregarProducto()
        }

        // Iniciar el listener para escuchar cambios en Firestore
        escucharCambiosEnFirestore()
    }

    private fun agregarProducto() {
        val nombre = binding.etNombreProducto.text.toString().trim()
        val precioTexto = binding.etPrecioProducto.text.toString().trim()
        val cantidadTexto = binding.etCantidadProducto.text.toString().trim()

        if (nombre.isEmpty() || precioTexto.isEmpty() || cantidadTexto.isEmpty()) {
            Toast.makeText(context, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val precio = precioTexto.toDoubleOrNull()
        val cantidad = cantidadTexto.toIntOrNull()

        if (precio == null || cantidad == null) {
            Toast.makeText(context, "Ingresa valores válidos para precio y cantidad", Toast.LENGTH_SHORT).show()
            return
        }

        val producto = Producto(
            id = "",  // Se asignará en el repositorio
            nombre = nombre,
            precio = precio,
            cantidad = cantidad,
            montoFinal = precio * cantidad,
            fotoUrl = ""  // Si hay imagen, manejarla aquí
        )

        // Llamar al ViewModel para agregar el producto
        productViewModel.agregarProducto(producto)
    }

    // Función para limpiar los campos después de agregar el producto
    private fun limpiarCampos() {
        binding.etNombreProducto.text.clear()
        binding.etPrecioProducto.text.clear()
        binding.etCantidadProducto.text.clear()
    }

    // Función para escuchar los cambios en Firestore
    private fun escucharCambiosEnFirestore() {
        val db = FirebaseFirestore.getInstance()
        val productosCollection = db.collection("productos")

        productosCollection.addSnapshotListener { snapshots, e ->
            if (e != null) {
                println("Error al escuchar cambios: $e")
                return@addSnapshotListener
            }

            if (snapshots != null) {
                val productos = snapshots.toObjects(Producto::class.java)
                CsvGenerator().generarCsv(requireContext(), productos)  // Generar CSV cada vez que hay un cambio
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
