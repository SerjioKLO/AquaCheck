package com.example.aquacheck.data

import com.example.aquacheck.model.PreChequeo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreChequeoRepository(private val dao: PreChequeoDao) {

    fun getAllPreChequeos(): Flow<List<PreChequeo>> {
        return dao.getAllPreChequeos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getPreChequeoById(id: String): PreChequeo? {
        return dao.getPreChequeoById(id)?.toDomain()
    }

    suspend fun insertPreChequeo(preChequeo: PreChequeo) {
        dao.insertPreChequeo(PreChequeoEntity.fromDomain(preChequeo))
    }

    suspend fun deletePreChequeo(id: String) {
        dao.deletePreChequeo(id)
    }
}
