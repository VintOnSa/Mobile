package com.example.desarrollomovil.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.desarrollomovil.data.JuegoRepository

class JuegoViewModelFactory(private val repository: JuegoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JuegoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JuegoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}