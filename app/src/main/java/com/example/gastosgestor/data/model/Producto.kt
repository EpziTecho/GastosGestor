package com.example.gastosgestor.data.model

data class Producto(
    var id: String = "",
    val nombre: String = "",
    val precio: Double = 0.0,
    val cantidad: Int = 0,
    val montoFinal: Double = 0.0,
    val fotoUrl: String = ""
)


