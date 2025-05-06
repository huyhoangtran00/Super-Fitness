package com.example.superfitness.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.superfitness.SuperFitnessApplication
import com.example.superfitness.viewmodel.RunViewModel

/**
 * Manual dependency injection
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            RunViewModel(
                superFitnessApplication().container.locationManager,
                superFitnessApplication().container.runRepository
            )
        }

        initializer {
            HomeViewModel(
                superFitnessApplication().container.runRepository
            )
        }
        initializer {
            RunDetailsViewModel(
                this.createSavedStateHandle(),
                superFitnessApplication().container.runRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.superFitnessApplication(): SuperFitnessApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as SuperFitnessApplication)