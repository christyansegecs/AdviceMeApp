package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.chris.adviceapp.R
import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.AdviceViewModelFactory

class MainActivity : AppCompatActivity() {

    private val retrofitService = AdviceService.getInstance()
    private lateinit var viewModel: AdviceViewModel
    private val tvAdvice by lazy { findViewById<TextView>(R.id.tvAdvice) }


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
            Log.d("apiResponse", "It Worked =D")
            tvAdvice.setText(viewModel.adviceString.value?.slip?.advice.toString())
        }
    }
}