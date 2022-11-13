package com.chris.adviceapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.AdviceViewModelFactory

class MainActivity : AppCompatActivity() {

    private val retrofitService = AdviceService.getInstance()
    private lateinit var viewModel: AdviceViewModel
    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }
    private val tvAdvice by lazy { findViewById<TextView>(R.id.tvAdvice) }
    private val btnNewAdvice by lazy { findViewById<Button>(R.id.btnNewAdvice) }
    private val btnSaveAdvice by lazy { findViewById<Button>(R.id.btnSaveAdvice) }
    private val btnList by lazy { findViewById<Button>(R.id.btnList) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this, AdviceViewModelFactory(AdviceRepository(retrofitService))).get(
            AdviceViewModel::class.java
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAdvice()
        viewModel.adviceString.observe(this) {
            tvAdvice.setText(viewModel.adviceString.value?.slip?.advice.toString())
        }

        btnNewAdvice.setOnClickListener { viewModel.getAdvice() }

        btnSaveAdvice.setOnClickListener {
            viewModelDB.insert(Advice(viewModel.adviceString.value?.slip?.advice.toString()))
        }

        btnList.setOnClickListener {
            val intent = Intent(this, SavedAdvicesActivity::class.java)
            startActivity(intent)
        }
    }
}