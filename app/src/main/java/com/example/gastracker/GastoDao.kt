package com.example.gastracker

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GastoDao {
    // CREATE: Inserta un nuevo registro de gasto
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarGasto(gasto: Gasto)

    // READ: Obtiene todos los gastos ordenados de más reciente a más antiguo
    @Query("SELECT * FROM gastos ORDER BY fechaTimestamp DESC")
    fun obtenerTodosLosGastos(): Flow<List<Gasto>>

    // READ: Obtiene un gasto por su ID
    @Query("SELECT * FROM gastos WHERE id = :id")
    suspend fun obtenerGastoPorId(id: Int): Gasto?

    // UPDATE: Actualiza un registro existente
    @Update
    suspend fun actualizarGasto(gasto: Gasto)

    // DELETE: Elimina un registro específico
    @Delete
    suspend fun eliminarGasto(gasto: Gasto)

    // DELETE: Elimina todos los registros de la tabla
    @Query("DELETE FROM gastos")
    suspend fun eliminarTodos()
}
