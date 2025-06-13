package com.example.superfitness.domain.usecases.run

import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository

class AddRun(
    private val repository: RunRepository
) {

    suspend operator fun invoke(runEntity: RunEntity) {
        repository.addRun(runEntity)
    }
}