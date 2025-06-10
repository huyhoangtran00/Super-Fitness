package com.example.superfitness.data.repository

import com.example.superfitness.data.local.dao.RunDao
import com.example.superfitness.data.local.entity.RunEntity
import com.example.superfitness.domain.repository.RunRepository
import kotlinx.coroutines.flow.Flow
class OfflineRunRepository(
    private val runDao: RunDao
) : RunRepository{
    override fun getRunStream(id: Int): Flow<RunEntity?> =
        runDao.getRun(id)

    override fun getRunsStream(): Flow<List<RunEntity>> =
        runDao.getAllRuns()
    override suspend fun addRun(runEntity: RunEntity) =
        runDao.insertRun(runEntity)

    override suspend fun removeRun(runEntity: RunEntity) =
        runDao.deleteRun(runEntity)

}