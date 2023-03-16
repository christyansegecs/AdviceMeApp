package com.chris.adviceapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityNewAdviceBinding
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class NewAdviceActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityNewAdviceBinding
    private val firebaseViewModel : FirebaseViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityNewAdviceBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val newAdvice = binding.tvNewAdvice.text

        binding.btnNewAdvice.setOnClickListener {
            val sdf = SimpleDateFormat("MMM dd,yyyy")
            val currentDate: String = sdf.format(Date())
            firebaseViewModel.saveAdvice(AdviceFirebase(newAdvice.toString(), currentDate))
            Toast.makeText(this, getString(R.string.toast_new_advice) , Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}