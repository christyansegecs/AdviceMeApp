package com.chris.adviceapp.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseStorage : FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStorage.reference
    private lateinit var binding: ActivitySignUpBinding
    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setButtonClickListener()
        registerActivityForResult()
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

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }

    private fun saveImageProfile() {
        val imageName = UUID.randomUUID().toString()
        val imageReference = storageReference.child("images").child(imageName)
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_LONG).show()
                val myUploadedImageReference = storageReference.child("images").child(imageName)
                myUploadedImageReference.downloadUrl.addOnSuccessListener { url ->
                    val imageURL = url.toString()
                    addUserToDatabase(imageURL)
                }.addOnFailureListener{
                    Toast.makeText(applicationContext, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun addUserToDatabase(url: String) {
        val email: String
        val password: String
//        val id: String = userReference.push().key.toString()
//        val advice = Advice(id, email, password, url)
//
//        userReference.child(id).setValue(advice).addOnCompleteListener { task ->
//
//            if (task.isSuccessful) {
//                Toast.makeText(applicationContext, "The new user has been added to the database", Toast.LENGTH_LONG).show()
//                finish()
//            } else {
//                Toast.makeText(applicationContext, task.exception.toString(), Toast.LENGTH_LONG).show()
//            }
//
//        }
    }

    private fun registerActivityForResult() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultCode = result.resultCode
            val imageData = result.data

            if (resultCode == RESULT_OK && imageData != null) {
                imageUri = imageData.data

                imageUri?.let {
                    Picasso.get().load(it).rotate(90F).into(binding.ivNewUser)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
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

        binding.ivNewUser.setOnClickListener{
            chooseImage()
        }
    }
}