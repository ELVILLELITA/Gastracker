package com.example.gastracker

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "gastos")
data class Gasto(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monto: Double,
    val tienda1: String,
    val tienda2: String,
    val etiqueta: String = "",
    val categoria: String = "Gasolina",
    val fechaTimestamp: Long = System.currentTimeMillis() // Guarda la fecha y hora exacta
) {
    // Función utilitaria para mostrar la fecha formateada en pantalla
    fun fechaFormateada(): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(fechaTimestamp))
    }
}