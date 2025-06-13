package com.example.superfitness.domain.usecases.run

import com.example.superfitness.domain.models.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow

class GetRun(
    private val repository: RunRepository
) {

    operator fun invoke(id: Int): Flow<RunEntity?> {
        return repository.getRunStream(id)
    }

}