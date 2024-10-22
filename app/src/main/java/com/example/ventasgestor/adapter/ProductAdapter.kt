package com.example.ventasgestor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ventasgestor.R
import com.example.ventasgestor.data.model.Producto
import com.bumptech.glide.Glide


class ProductAdapter(private var productos: List<Producto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val precioProducto: TextView = itemView.findViewById(R.id.tvPrecioProducto)
        val imagenProducto: ImageView = itemView.findViewById(R.id.ivImagenProducto)
        val cantidadProducto: TextView = itemView.findViewById(R.id.tvCantidadProducto)
        val montoTotalProducto: TextView = itemView.findViewById(R.id.tvMontoTotalProducto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_producto, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombreProducto.text = producto.nombre
        holder.precioProducto.text = "S/. ${producto.precio}"
        holder.cantidadProducto.text = "Cantidad: ${producto.cantidad}"
        holder.montoTotalProducto.text = "Total: S/. ${producto.montoFinal}"

        // Cargar la imagen usando Glide (o Picasso)
        if (producto.fotoUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(producto.fotoUrl)
                .placeholder(R.drawable.ic_launcher_background)  // Imagen de placeholder
                .error(R.drawable.ic_launcher_foreground)  // Imagen en caso de error
                .into(holder.imagenProducto)
        } else {
            holder.imagenProducto.setImageResource(R.drawable.ic_launcher_background)  // Si no hay URL de imagen
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    // Función para actualizar la lista de productos
    fun setProductos(productosActualizados: List<Producto>) {
        this.productos = productosActualizados
        notifyDataSetChanged()
    }
}
