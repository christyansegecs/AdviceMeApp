package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityForgetBinding
import com.google.firebase.auth.FirebaseAuth

class ForgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgetBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        binding.btnReset.setOnClickListener {
            val email = binding.tvForget.text.toString()

            if (email.isEmpty()) {
                binding.tvForget.error = getString(R.string.error_input_email)
                binding.tvForget.requestFocus()
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener {
                        task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext,
                            getString(R.string.toast_sent_password),
                            Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}