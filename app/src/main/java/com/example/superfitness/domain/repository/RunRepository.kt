package com.example.superfitness.domain.repository

import com.example.superfitness.data.local.entity.RunEntity
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRunStream(id: Int): Flow<RunEntity?>
    fun getRunsStream(): Flow<List<RunEntity>>
    suspend fun addRun(runEntity: RunEntity)
    suspend fun removeRun(runEntity: RunEntity)
}