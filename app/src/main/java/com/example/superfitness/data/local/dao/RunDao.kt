package com.example.superfitness.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.superfitness.data.local.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Query("SELECT * FROM run_activities")
    fun getAllRuns(): Flow<List<RunEntity>>

    @Query("SELECT * FROM run_activities WHERE id = :id")
    fun getRun(id: Int): Flow<RunEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(item: RunEntity)

    @Delete
    suspend fun deleteRun(item: RunEntity)
}