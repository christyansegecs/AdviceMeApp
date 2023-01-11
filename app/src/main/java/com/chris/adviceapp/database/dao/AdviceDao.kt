package com.chris.adviceapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.chris.adviceapp.database.models.Advice
import kotlinx.coroutines.flow.Flow

@Dao
interface AdviceDao {

    @Query("SELECT * FROM advice_table ORDER BY id ASC")
    fun getAlphabetizedAdvices(): Flow<List<Advice>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(advice: Advice)

    @Delete
    suspend fun delete(advice: Advice)

}