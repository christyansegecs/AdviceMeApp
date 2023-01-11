package com.chris.adviceapp.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.adapter.AdviceListAdapter
import com.chris.adviceapp.databinding.ActivitySavedAdvicesBinding
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory

class SavedAdvicesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdviceListAdapter
    private lateinit var binding: ActivitySavedAdvicesBinding
    private val adviceDatabaseViewModel : AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySavedAdvicesBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.recyclerView = this.binding.rvAdvice
        this.adapter = AdviceListAdapter { advice ->
            adapter.deleteAdvice(advice)
            adviceDatabaseViewModel.delete(advice)
        }
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onStart() {
        super.onStart()

        adviceDatabaseViewModel.allAdvices.observe(this) { advices ->
            advices?.let { adapter.submitList(it) }
        }
    }
}