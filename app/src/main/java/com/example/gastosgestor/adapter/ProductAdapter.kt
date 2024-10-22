package com.example.gastosgestor.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gastosgestor.R
import com.example.gastosgestor.data.model.Producto

class ProductAdapter(private var productos: List<Producto>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreProducto: TextView = itemView.findViewById(R.id.tvNombreProducto)
        val precioProducto: TextView = itemView.findViewById(R.id.tvPrecioProducto)
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
