package com.example.aquacheck.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.aquacheck.data.AppDatabase
import com.example.aquacheck.data.PreChequeoRepository
import com.example.aquacheck.model.MockData
import com.example.aquacheck.model.PreChequeo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalleViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val repository: PreChequeoRepository

    private val _chequeo = MutableStateFlow<PreChequeo?>(null)
    val chequeo: StateFlow<PreChequeo?> = _chequeo.asStateFlow()

    init {
        val dao = AppDatabase.getDatabase(application).preChequeoDao()
        repository = PreChequeoRepository(dao)
        val id: String = savedStateHandle.get<String>("id") 
            ?: savedStateHandle.get<Int>("id")?.toString() 
            ?: ""
        if (id.isNotBlank()) {
            cargarDetalle(id)
        }
    }

    fun cargarDetalle(id: String) {
        viewModelScope.launch {
            val entity = repository.getPreChequeoById(id)
            if (entity != null) {
                _chequeo.value = entity
            } else {
                val mock = MockData.mockPreChequeos.find { it.id == id }
                _chequeo.value = mock ?: MockData.mockPreChequeos.firstOrNull()
            }
        }
    }

    fun eliminarChequeo(onDeleted: () -> Unit) {
        val currentChequeo = _chequeo.value ?: return
        viewModelScope.launch {
            repository.deletePreChequeo(currentChequeo.id)
            onDeleted()
        }
    }
}
