package com.chris.adviceapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chris.adviceapp.database.AdviceRoomDatabase
import com.chris.adviceapp.database.dao.AdviceDao
import org.junit.Before
import org.junit.runner.RunWith
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.chris.adviceapp.database.models.Advice
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import org.junit.Assert.assertEquals
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class AdviceDaoTest {

    private lateinit var adviceDao: AdviceDao
    private lateinit var db: AdviceRoomDatabase

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AdviceRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        adviceDao = db.adviceDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetAdvice() = runBlocking {
        val advice = Advice("word")
        adviceDao.insert(advice)
        val allAdvices = adviceDao.getAlphabetizedAdvices().first()
        assertEquals(allAdvices[0].advice, advice.advice)
    }

    @Test
    @Throws(Exception::class)
    fun getAllAdvices() = runBlocking {
        val advice1 = Advice("aaa")
        adviceDao.insert(advice1)
        val advice2 = Advice("bbb")
        adviceDao.insert(advice2)
        val allWords = adviceDao.getAlphabetizedAdvices().first()
        assertEquals(allWords[0].advice, advice1.advice)
        assertEquals(allWords[1].advice, advice2.advice)
    }

}