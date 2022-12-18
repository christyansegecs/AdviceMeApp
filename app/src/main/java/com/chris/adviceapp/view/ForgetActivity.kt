package com.chris.adviceapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                task ->
                if (task.isSuccessful) {
                    Toast.makeText(applicationContext,
                    "We sent a password reset email for your email address",
                    Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        binding.btnPhone.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }
    }
}