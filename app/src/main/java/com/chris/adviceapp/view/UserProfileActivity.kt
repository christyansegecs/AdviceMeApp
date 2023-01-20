package com.chris.adviceapp.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.databinding.ActivityUserProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class UserProfileActivity  : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users/profileImageUrl")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Picasso.get().load(snapshot.value.toString()).rotate(90F).into(binding.ivUserProfile)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("snapshot", "something went wrong")
            }
        })
    }
}