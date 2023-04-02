package com.chris.adviceapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.chris.adviceapp.databinding.ActivityFriendProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class FriendProfileActivity  : AppCompatActivity()  {

    val auth = FirebaseAuth.getInstance()
    private val databaseUserRef = FirebaseDatabase.getInstance().getReference("users")
    private val databaseRequestRef = FirebaseDatabase.getInstance().getReference("friend_request")
    private lateinit var binding: ActivityFriendProfileBinding
    var CURRENT_STATE = "not friends"
    private lateinit var userEmail: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        userEmail = intent.getStringExtra("user").toString()

        fetchUserFromDatabase()
        setupButtons()
        setupClickListener()
    }

    private fun fetchUserFromDatabase() {

        val query: Query = databaseUserRef.orderByChild("userEmail")
            .equalTo(userEmail)
        query.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val userName = ds.child("userName").value.toString()
                    val userEmail = ds.child("userEmail").value.toString()
                    val profileImageUrl = ds.child("profileImageUrl").value.toString()
                    userId = ds.key.toString()
                    binding.tvUserName.text = userName
                    binding.tvUserEmail.text = userEmail
                    Picasso.get().load(profileImageUrl).into(binding.ivUserProfile)
                    if (auth.uid == userId) {
                        binding.btnAddFriend.isVisible = false
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun setupButtons() {

        auth.uid?.let {
            databaseRequestRef.child(it)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val request = snapshot.child(userId).child(STATE_REQUEST).value.toString()
                        if (request == STATE_SENT) {
                            CURRENT_STATE = REQUEST_SENT
                            binding.btnAddFriend.text = CANCEL_REQUEST
                            binding.btnRefuseFriend.isVisible = false
                            cancelFriendRequest()
                        } else if (request == STATE_RECEIVED) {
                            CURRENT_STATE = REQUEST_RECEIVED
                            binding.btnAddFriend.text = "Accept Friend Request"
                            binding.btnRefuseFriend.isVisible = true
                            acceptFriendRequest()
                            declineFriendRequest()
                        } else if (request == STATE_FRIENDS) {
                            CURRENT_STATE = STATE_FRIENDS
                            binding.btnAddFriend.text = "Unfriend this Person"
                            unfriend()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    private fun setupClickListener() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { senderId ->
                databaseRequestRef.child(senderId).
                child(userId).child(STATE_REQUEST).setValue(STATE_SENT)
                    .addOnCompleteListener {
                        databaseRequestRef.child(userId).
                        child(senderId).child(STATE_REQUEST).setValue(STATE_RECEIVED)
                        CURRENT_STATE = REQUEST_SENT
                        binding.btnAddFriend.text = CANCEL_REQUEST
                    }
            }
        }
    }

    private fun cancelFriendRequest() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue() }
            binding.btnAddFriend.text = "Add Friend"
            CURRENT_STATE = STATE_NOT_FRIENDS
        }
    }

    private fun acceptFriendRequest() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).child(STATE_REQUEST).setValue("friends")
                databaseRequestRef.child(userId).child(it1).child(STATE_REQUEST).setValue("friends")
            databaseUserRef.child(it1).child("Friends").push().setValue(userId)
                databaseUserRef.child(userId).child("Friends").push().setValue(it1) }
            binding.btnAddFriend.text = "Unfriend this Person"
            CURRENT_STATE = STATE_FRIENDS
        }
    }

    private fun declineFriendRequest() {
        binding.btnRefuseFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue() }
            binding.btnAddFriend.text = "Add Friend"
            CURRENT_STATE = STATE_NOT_FRIENDS
            binding.btnRefuseFriend.isVisible = false
        }
        setupButtons()
    }

    private fun unfriend() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue()
                databaseRequestRef.child(userId).child(it1).removeValue()
                binding.btnAddFriend.text = "Add Friend"
                CURRENT_STATE = STATE_NOT_FRIENDS
            }
            setupButtons()
        }
    }

    companion object {
        const val STATE_REQUEST = "request"
        const val STATE_RECEIVED = "received"
        const val STATE_SENT = "sent"
        const val REQUEST_SENT = "request sent"
        const val REQUEST_RECEIVED = "request received"
        const val CANCEL_REQUEST = "cancel friend request"
        const val STATE_NOT_FRIENDS = "not friends"
        const val STATE_FRIENDS = "friends"
    }
}