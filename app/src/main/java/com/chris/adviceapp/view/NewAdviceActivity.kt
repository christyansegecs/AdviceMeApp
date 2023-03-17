package com.chris.adviceapp.view

import android.content.Intent
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

        setupClickListener()
    }

    private fun setupClickListener() {

        binding.btnNewAdvice.setOnClickListener {
            val sdf = SimpleDateFormat("MMM dd,yyyy")
            val currentDate: String = sdf.format(Date())
            val newAdvice = binding.tvNewAdvice.text
            firebaseViewModel.saveAdvice(AdviceFirebase(newAdvice.toString(), currentDate))
            Toast.makeText(this, getString(R.string.toast_new_advice) , Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnShare.setOnClickListener {
            val newAdvice = binding.tvNewAdvice.text
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("share", newAdvice.toString())
            startActivity(intent)
            finish()
        }
    }
}