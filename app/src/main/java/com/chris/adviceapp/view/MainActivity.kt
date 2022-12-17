package com.chris.adviceapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.databinding.ActivityLoginBinding
import com.chris.adviceapp.databinding.ActivityMainBinding
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.util.AdviceState
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.AdviceViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val retrofitService = AdviceService.getInstance()
    private lateinit var viewModel: AdviceViewModel
    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }
    private lateinit var currentAdvice : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setupViewModel()
        viewModel.getAdvice()
        handleAdvices()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAdvice()

        binding.btnNewAdvice.setOnClickListener { viewModel.getAdvice() }

        binding.btnSaveAdvice.setOnClickListener {
            viewModelDB.insert(Advice(currentAdvice))
        }

        binding.btnList.setOnClickListener {
            val intent = Intent(this, SavedAdvicesActivity::class.java)
            startActivity(intent)
        }
    }

    fun handleAdvices() = lifecycleScope.launchWhenCreated{
        viewModel._adviceStateFlow.collect {
            when(it) {
                is AdviceState.onLoading -> { binding.progressBar.isVisible=true }
                is AdviceState.onError -> {
                    AdviceState.onError(error("Error. Please try again"))
                    binding.progressBar.isVisible = false }
                is AdviceState.onSuccess -> {
                    it.data.body()?.slip?.advice
                    binding.tvAdvice.setText(it.data.body()?.slip?.advice)
                    currentAdvice = it.data.body()?.slip?.advice.toString()
                    binding.progressBar.isVisible=false
                    }
                is AdviceState.Empty -> {}
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, AdviceViewModelFactory(AdviceRepository(retrofitService))).get(
            AdviceViewModel::class.java
        )
    }

}