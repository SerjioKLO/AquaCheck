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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreChequeo(entity: PreChequeoEntity)

    @Query("DELETE FROM prechequeos WHERE id = :id")
    suspend fun deletePreChequeo(id: String)
}
