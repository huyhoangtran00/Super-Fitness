package com.example.superfitness.domain.usecases.run

import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository

class DeleteRun(
    private val repository: RunRepository
) {

    suspend operator fun invoke(runEntity: RunEntity) {
        repository.removeRun(runEntity)
    }
}