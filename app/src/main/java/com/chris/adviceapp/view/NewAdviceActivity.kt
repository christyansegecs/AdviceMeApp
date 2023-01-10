package com.chris.adviceapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.databinding.ActivityNewAdviceBinding
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory

class NewAdviceActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityNewAdviceBinding
    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityNewAdviceBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val newAdvice = binding.tvNewAdvice.text

        binding.btnNewAdvice.setOnClickListener {
            viewModelDB.insert(Advice(newAdvice.toString()))
            Toast.makeText(this, getString(R.string.toast_new_advice) , Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}