package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityForgetBinding
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ForgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetBinding
    private val firebaseViewModel : FirebaseViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
        firebaseViewModel.getCurrentUser()
        setupClickListener()
    }

    private fun setupClickListener() {
        binding.btnReset.setOnClickListener {
            val email = binding.tvForget.text.toString()

            if (email.isEmpty()) {
                binding.tvForget.error = getString(R.string.error_input_email)
                binding.tvForget.requestFocus()
            } else {
                firebaseViewModel.sendNewPasswordToEmail(email)
                Toast.makeText(applicationContext,
                    getString(R.string.toast_sent_password),
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}