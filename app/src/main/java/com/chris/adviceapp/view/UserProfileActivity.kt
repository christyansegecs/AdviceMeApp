package com.chris.adviceapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityUserProfileBinding
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class UserProfileActivity  : AppCompatActivity() {

    private val firebaseViewModel : FirebaseViewModel by viewModel()
    private lateinit var binding: ActivityUserProfileBinding
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private val databaseUserPictureRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/profileImageUrl")
    private val databaseUserNameRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/userName")
    private var imageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        firebaseViewModel.getCurrentUser()
        fetchUserProfilePicture()
        fetchUserName()
        setOnClickListeners()
    }

    private fun fetchUserProfilePicture() {
        databaseUserPictureRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.value.toString()).into(binding.ivUserProfile)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("snapshot", "something went wrong")
            }
        })
    }

    private fun fetchUserName() {
        databaseUserNameRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.value.toString()
                binding.tvUserName.text = userName
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("snapshot", "something went wrong")
            }
        })
    }

    private fun setOnClickListeners() {
        binding.tvChangeImageUser.setOnClickListener{
            chooseImage()
        }
        binding.tvChangeUserName.setOnClickListener{
            uploadUserNameInDatabase()
        }
        binding.icCheck.setOnClickListener{
            val newUserName = binding.editUserName.text.toString()
            binding.tvUserName.text = newUserName
            val ref = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/userName")
            ref.setValue(newUserName)
            changeViewVisibility()
            Toast.makeText(
                applicationContext, getString(R.string.toast_username_uploaded),
                Toast.LENGTH_LONG
            ).show()
        }
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
                Picasso.get().load(it).into(binding.ivUserProfile)
                uploadImageToFirebaseStorage()
            }
        }
    }

    private fun uploadImageToFirebaseStorage() {
        if (imageUri == null) return
        val filename = UUID.randomUUID().toString()
        val imageReference = FirebaseStorage.getInstance().getReference("/images/$filename")

        imageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                Log.d("firebase", "Successfully uploaded image: ${it.metadata?.path}")

                imageReference.downloadUrl.addOnSuccessListener {
                    Log.d("firebase", "File Location: $it")
                    updateImageDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("firebase", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun updateImageDatabase(url: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/profileImageUrl")
        ref.setValue(url)
        Toast.makeText(
            applicationContext, getString(R.string.toast_picture_uploaded),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun uploadUserNameInDatabase() {
        binding.tvUserName.isVisible = false
        binding.editUserName.isVisible = true
        binding.icCheck.isVisible = true
    }

    private fun changeViewVisibility() {
        binding.editUserName.isVisible = false
        binding.icCheck.isVisible = false
        binding.tvUserName.isVisible = true
    }

    companion object {
        const val URL_PICTURE_DEFAULT = "http://www.univates.br/roau/download/147/calvin/calvin.jpg"
    }
}