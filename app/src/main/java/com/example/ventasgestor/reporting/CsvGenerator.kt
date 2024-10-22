package com.example.ventasgestor.csv

import android.content.Context
import android.os.Environment
import com.example.ventasgestor.data.model.Producto
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CsvGenerator {

    fun generarCsv(context: Context, productos: List<Producto>) {
        val csvHeader = "ID,Nombre,Precio,Cantidad,MontoFinal,FotoURL\n"
        val sb = StringBuilder()
        sb.append(csvHeader)

        // Convertir cada producto en una fila de CSV
        for (producto in productos) {
            sb.append("${producto.id},${producto.nombre},${producto.precio},${producto.cantidad},${producto.montoFinal},${producto.fotoUrl}\n")
        }

        // Crear la carpeta específica en almacenamiento local
        try {
            val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!
            val carpetaEspecifica = File(storageDir, "informes_productos")
            if (!carpetaEspecifica.exists()) {
                carpetaEspecifica.mkdirs()  // Crear la carpeta si no existe
            }

            val archivoCsv = File(carpetaEspecifica, "productos.csv")

            val writer = FileWriter(archivoCsv)
            writer.append(sb.toString())
            writer.flush()
            writer.close()

            println("CSV generado con éxito en: ${archivoCsv.absolutePath}")
        } catch (e: IOException) {
            e.printStackTrace()
            println("Error al generar el archivo CSV")
        }
    }
}
