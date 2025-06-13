package com.example.superfitness.domain.usecases.run

import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class GetAllRuns(
    private val repository: RunRepository
) {

    operator fun invoke(): Flow<List<RunEntity>> {
        return repository.getRunsStream()
    }
}