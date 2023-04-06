package com.chris.adviceapp.view

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.chris.adviceapp.api.NotificationRetrofitInstance
import com.chris.adviceapp.databinding.ActivityFriendProfileBinding
import com.chris.adviceapp.model.NotificationData
import com.chris.adviceapp.model.PushNotification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FriendProfileActivity  : AppCompatActivity()  {

    private val TAG = "FriendProfileActivity"
    val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityFriendProfileBinding
    var CURRENT_STATE = "not friends"
    private lateinit var userEmail: String
    private var userId = "userId"
    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }
    private val databaseUserRef = FirebaseDatabase.getInstance().getReference("users")
    private val databaseRequestRef = FirebaseDatabase.getInstance().getReference("friend_request")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        userEmail = intent.getStringExtra("user").toString()

        fetchUserFromDatabase()
        setupAddFriend()
        setupButtons()
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
                    Glide.with(this@FriendProfileActivity).load(profileImageUrl).into((binding.ivUserProfile))
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

    private fun setupAddFriend() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { senderId ->
                databaseRequestRef.child(senderId).
                child(userId).child(STATE_REQUEST).setValue(STATE_SENT)
                    .addOnCompleteListener {
                        databaseRequestRef.child(userId).
                        child(senderId).child(STATE_REQUEST).setValue(STATE_RECEIVED)
                        CURRENT_STATE = REQUEST_SENT
                        binding.btnAddFriend.text = CANCEL_REQUEST
                        cancelFriendRequest()

                        databaseUserRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val token = snapshot.child("token").value.toString()
                                val title = "New Friend Request"
                                val message = "An User wants to be your Friend"

                                if(title.isNotEmpty() && message.isNotEmpty() && token.isNotEmpty()) {
                                    PushNotification(
                                        NotificationData(title, message),
                                        token
                                    ).also {
                                        sendNotification(it)
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
            }
        }
    }

    private fun cancelFriendRequest() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue() }
            binding.btnAddFriend.text = "Add Friend"
            CURRENT_STATE = STATE_NOT_FRIENDS
            setupAddFriend()
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
            binding.btnRefuseFriend.isVisible = false
            unfriend()
        }
    }

    private fun declineFriendRequest() {
        binding.btnRefuseFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue()
            databaseRequestRef.child(userId).child(it1).removeValue()}
            binding.btnAddFriend.text = "Add Friend"
            CURRENT_STATE = STATE_NOT_FRIENDS
            binding.btnRefuseFriend.isVisible = false
            setupAddFriend()
        }
    }

    private fun unfriend() {
        binding.btnAddFriend.setOnClickListener {
            auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue()
                databaseRequestRef.child(userId).child(it1).removeValue()


                databaseUserRef.child(it1).child("Friends")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(ds: DataSnapshot) {
                            ds.child(userId).children
                            for (ds in ds.children) {
                                if (ds.value.toString() == userId) {
                                    ds.ref.removeValue()
                            }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                databaseUserRef.child(userId).child("Friends")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(ds: DataSnapshot) {
                            ds.child(it1).children
                            for (ds in ds.children) {
                                if (ds.value.toString() == userId) {
                                    ds.ref.removeValue()
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                databaseUserRef.child(it1).child("Friends").child(userId).ref.removeValue()
                databaseUserRef.child(userId).child("Friends").child(it1).ref.removeValue()
                binding.btnAddFriend.text = "Add Friend"
                CURRENT_STATE = STATE_NOT_FRIENDS
                setupAddFriend()
            }
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(
        Dispatchers.IO + coroutineExceptionHandler).launch {
        try {
            val response = NotificationRetrofitInstance.api.postNotification(notification)
            Log.d(TAG,"Response: $response")

            if(response.isSuccessful) {
                Log.d(TAG,"Response: ${Gson().toJson(response)}")
            } else {
                Log.d(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
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