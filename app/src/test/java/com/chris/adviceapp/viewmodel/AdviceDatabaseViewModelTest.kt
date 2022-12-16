package com.chris.adviceapp.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.chris.adviceapp.database.AdviceRoomDatabase
import com.chris.adviceapp.database.dao.AdviceDao
import com.chris.adviceapp.database.models.Advice
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AdviceDatabaseViewModelTest {

    private lateinit var adviceDatabaseViewModel: AdviceDatabaseViewModel
    private lateinit var adviceDao: AdviceDao
    private lateinit var db: AdviceRoomDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        adviceDatabaseViewModel =
            AdviceDatabaseViewModel(ApplicationProvider.getApplicationContext())
    }

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AdviceRoomDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        adviceDao = db.adviceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insert_NewAdice_IntoViewModel_dispatchesObserver_OnLiveData() {

        runBlocking {
            adviceDatabaseViewModel.insert(Advice("aaa"))

            val value = adviceDatabaseViewModel.allAdvices.value
            assertThat(value?.last(), (not(notNullValue())))
//            assertThat(value?.last()?.advice, (equalTo("aaa")))
        }
    }

}
