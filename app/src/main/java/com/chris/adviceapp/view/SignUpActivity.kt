package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setButtonClickListener()
    }

    private fun signUpWithFirebase(userEmail: String, password: String) {
        auth.createUserWithEmailAndPassword(userEmail, password).
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext,getString(R.string.signup_successful),
                    Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(applicationContext,task.exception?.toString(),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setButtonClickListener() {

        binding.btnSignUp.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val password = binding.password.text.toString()

            if (userEmail.isEmpty()) {
                binding.userEmail.error = getString(R.string.error_input_an_email)
                binding.userEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.password.error = getString(R.string.error_input_an_password)
                binding.password.requestFocus()
            } else {
                signUpWithFirebase(userEmail, password)
            }
        }

        binding.icRedEye.setOnClickListener{
            if (binding.password.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_hide)
            } else {
                binding.password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_remove_red_eye)
            }
        }
    }
}