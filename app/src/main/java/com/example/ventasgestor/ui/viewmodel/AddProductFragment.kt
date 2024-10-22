import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.example.ventasgestor.csv.CsvGenerator
import com.example.ventasgestor.data.model.Producto
import com.example.ventasgestor.databinding.FragmentAddProductBinding
import com.example.ventasgestor.ui.viewmodel.ProductViewModel
import com.example.ventasgestor.ui.viewmodel.ProductViewModelFactory
import com.example.ventasgestor.data.repository.ProductRepository
import androidx.lifecycle.ViewModelProvider
import com.example.ventasgestor.R
import com.example.ventasgestor.ui.view.MainActivity

class AddProductFragment : Fragment() {

    private lateinit var productViewModel: ProductViewModel
    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private var imageUri: Uri? = null  // Variable para almacenar la URI de la imagen seleccionada

    companion object {
        private const val PICK_IMAGE_REQUEST = 1  // Código de solicitud para seleccionar imagen
    }

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
            requireActivity().supportFragmentManager.popBackStack()
            (requireActivity() as MainActivity).mostrarElementosMainActivity() // Mostrar RecyclerView y botón en MainActivity
        }

        // Configurar el botón para agregar el producto
        binding.btnAgregarProducto.setOnClickListener {
            agregarProducto()
        }

        // Configurar el ImageView para seleccionar una imagen de la galería
        binding.ivImagenProducto.setOnClickListener {
            seleccionarImagenDeGaleria()
        }

        // Iniciar el listener para escuchar cambios en Firestore
        escucharCambiosEnFirestore()
    }

    // Función para abrir la galería y seleccionar una imagen
    private fun seleccionarImagenDeGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Capturar el resultado de seleccionar la imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data  // Guardar la URI de la imagen seleccionada
            binding.ivImagenProducto.setImageURI(imageUri)  // Mostrar la imagen en el ImageView
        }
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
            fotoUrl = ""  // Se actualizará después con la URL de Firebase
        )

        // Llamar al ViewModel para agregar el producto con la imagen seleccionada
        productViewModel.agregarProducto(producto, imageUri)
    }

    // Función para limpiar los campos después de agregar el producto
    private fun limpiarCampos() {
        binding.etNombreProducto.text.clear()
        binding.etPrecioProducto.text.clear()
        binding.etCantidadProducto.text.clear()
        imageUri = null
        binding.ivImagenProducto.setImageResource(R.drawable.ic_launcher_background)  // Resetear la imagen
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
