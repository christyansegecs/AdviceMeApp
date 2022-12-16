package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.chris.adviceapp.databinding.ActivityLoginBinding
import com.chris.adviceapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        binding.btnSignUp.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val password = binding.password.text.toString()
            signUpWithFirebase(userEmail, password)
        }

    }

    fun signUpWithFirebase(userEmail: String, password: String) {
        auth.createUserWithEmailAndPassword(userEmail, password).
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext,"Your account has been created",
                    Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(applicationContext,task.exception?.toString(),
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}