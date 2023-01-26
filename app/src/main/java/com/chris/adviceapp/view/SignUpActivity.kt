package com.chris.adviceapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivitySignUpBinding
import com.chris.adviceapp.usermodel.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private var imageUri : Uri? = null
    val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setButtonClickListener()
    }

    private fun chooseImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            imageUri?.let {
                Picasso.get().load(it).rotate(90F).into(binding.ivNewUser)
            }
        }
    }

    private fun signUpWithFirebase(userEmail: String, password: String) {
        val auth: FirebaseAuth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(userEmail, password).
        addOnCompleteListener { task ->
            if (task.isSuccessful) {
                uploadImageToFirebaseStorage()
                Toast.makeText(applicationContext,getString(R.string.signup_successful),
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(applicationContext,task.exception?.toString(),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUri == null) {
            imageUri = Uri.parse("http://www.univates.br/roau/download/147/calvin/calvin.jpg")
            saveUserToFirebaseDatabase(imageUri.toString())
        } else {
            val filename = UUID.randomUUID().toString()
            val imageReference = FirebaseStorage.getInstance().getReference("/images/$filename")
            imageReference.putFile(imageUri!!)
                .addOnSuccessListener {
                    Log.d("firebase", "Successfully uploaded image: ${it.metadata?.path}")

                    imageReference.downloadUrl.addOnSuccessListener {
                        Log.d("firebase", "File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("firebase", "Failed to upload image to storage: ${it.message}")
                }
        }
    }

    private fun saveUserToFirebaseDatabase(url: String) {
        val userName = binding.tvUserName.text.toString()
        val userEmail = binding.tvUserEmail.text.toString()
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Log.d("authentication", user?.uid.toString())
        val ref = FirebaseDatabase.getInstance().getReference("users/${user?.uid}")
        val userCreated = User(userName, userEmail, url)
        ref.setValue(userCreated)
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setButtonClickListener() {

        binding.btnSignUp.setOnClickListener {
            val userEmail = binding.tvUserEmail.text.toString()
            val password = binding.tvPassword.text.toString()
            val userName = binding.tvUserName.text.toString()

            if (userEmail.isEmpty()) {
                binding.tvUserEmail.error = getString(R.string.error_input_an_email)
                binding.tvUserEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.tvPassword.error = getString(R.string.error_input_an_password)
                binding.tvPassword.requestFocus()
            } else if (userName.isEmpty()) {
                binding.tvUserName.error = getString(R.string.error_input_an_user_name)
                binding.tvUserName.requestFocus()
            } else {
                signUpWithFirebase(userEmail, password)
            }
        }

        binding.icRedEye.setOnClickListener{
            if (binding.tvPassword.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                binding.tvPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_hide)
            } else {
                binding.tvPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_remove_red_eye)
            }
        }

        binding.ivNewUser.setOnClickListener{
            chooseImage()
        }
    }
}