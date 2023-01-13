package com.chris.adviceapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chris.adviceapp.database.dao.AdviceDao
import com.chris.adviceapp.database.models.Advice

@Database(entities = [Advice::class], version = 1)
abstract class AdviceRoomDatabase : RoomDatabase() {

    abstract fun adviceDao() : AdviceDao

    companion object {

        private const val DATABASE_NAME: String = "AdviceDB"

        @Volatile
        private var INSTANCE: AdviceRoomDatabase? = null

        fun getDatabase(context: Context): AdviceRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdviceRoomDatabase::class.java, DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}