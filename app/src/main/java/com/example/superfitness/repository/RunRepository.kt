package com.example.superfitness.repository

import com.example.superfitness.data.local.db.dao.RunDao
import com.example.superfitness.data.local.db.entity.RunEntity
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRunStream(id: Int): Flow<RunEntity?>
    fun getRunsStream(): Flow<List<RunEntity>>
    suspend fun addRun(runEntity: RunEntity)
    suspend fun removeRun(runEntity: RunEntity)
}
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