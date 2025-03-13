package com.example.superfitness.data.local.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.superfitness.data.local.db.entity.WaterIntake

@Dao
interface WaterIntakeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(waterIntake: WaterIntake) : Long

    @Update
    suspend fun update(waterIntake: WaterIntake) : Int

    @Delete
    suspend fun delete(waterIntake: WaterIntake) : Int

    @Query("SELECT * FROM water_intake WHERE date = :date")
    suspend fun getWaterIntakeByDate(date: String): WaterIntake?

    @Query("SELECT * FROM water_intake")
    fun getAllWaterIntakes(): LiveData<List<WaterIntake>>
}
