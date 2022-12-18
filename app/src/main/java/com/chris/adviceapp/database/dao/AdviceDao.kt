package com.chris.adviceapp.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy
import com.chris.adviceapp.database.models.Advice
import kotlinx.coroutines.flow.Flow

@Dao
interface AdviceDao {

    @Query("SELECT * FROM advice_table ORDER BY advice ASC")
    fun getAlphabetizedAdvices(): Flow<List<Advice>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(advice: Advice)

}