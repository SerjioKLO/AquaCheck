package com.example.aquacheck.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PreChequeoDao {
    @Query("SELECT * FROM prechequeos ORDER BY fecha DESC")
    fun getAllPreChequeos(): Flow<List<PreChequeoEntity>>

    @Query("SELECT * FROM prechequeos WHERE id = :id")
    suspend fun getPreChequeoById(id: String): PreChequeoEntity?

    @Query("SELECT * FROM prechequeos WHERE sincronizado = 0")
    suspend fun getPreChequeosPendientes(): List<PreChequeoEntity>

    @Query("UPDATE prechequeos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: String)

    @Query("UPDATE prechequeos SET evidenciasJson = :evidenciasJson, sincronizado = 1 WHERE id = :id")
    suspend fun actualizarEvidenciasYSincronizado(id: String, evidenciasJson: String)

    @Query("DELETE FROM prechequeos WHERE id LIKE 'pc%' OR id LIKE 'u%'")
    suspend fun deleteAllMockData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreChequeo(entity: PreChequeoEntity)

    @Query("DELETE FROM prechequeos WHERE id = :id")
    suspend fun deletePreChequeo(id: String)
}
