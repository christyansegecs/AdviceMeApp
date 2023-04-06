package com.chris.adviceapp.view

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.adapter.*
import com.chris.adviceapp.databinding.ActivityFriendsBinding
import com.chris.adviceapp.databinding.CustomBottomSheetBinding
import com.chris.adviceapp.usermodel.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class FriendsActivity : AppCompatActivity(),
    FriendClickInterface,
    FriendRequestAcceptInterface,
    FriendRequestRefuseInterface,
    FriendRequestProfileInterface
{

    private lateinit var binding: ActivityFriendsBinding
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var friendsRequestsRecyclerView: RecyclerView
    private lateinit var friendsAdapter: FriendsAdapter
    private lateinit var friendsRequestsAdapter: FriendsRequestAdapter
    val allFriends = ArrayList<User>()
    val allFriendsRequests = ArrayList<User>()
    val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseUserRef = FirebaseDatabase.getInstance().getReference("users")
    private val databaseAdvicesRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/Friends")
    private val databaseRequestRef = FirebaseDatabase.getInstance().getReference("friend_request")
    private val databaseFriendsRequestsRef = FirebaseDatabase.getInstance().getReference("friend_request/${user?.uid}")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.friendsRecyclerView = this.binding.rvFriends
        this.friendsAdapter = FriendsAdapter(this, this)
        this.friendsRecyclerView.adapter = this.friendsAdapter
        this.friendsRecyclerView.layoutManager = LinearLayoutManager(this)

        this.friendsRequestsRecyclerView = this.binding.rvFriendsRequest
        this.friendsRequestsAdapter = FriendsRequestAdapter(this, this, this, this)
        this.friendsRequestsRecyclerView.adapter = this.friendsRequestsAdapter
        this.friendsRequestsRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchFriendsFromDatabase()
        fetchFriendsRequestsFromDatabase()

        binding.svFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
        setupBottomNavigationBar()
    }

    private fun fetchFriendsFromDatabase() {
        databaseAdvicesRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {

                    val userFriendId = ds.value.toString()
                    val databaseFriendsRef = FirebaseDatabase.getInstance().getReference("users/${userFriendId}")

                    databaseFriendsRef.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                                val userName = snapshot.child("userName").value.toString()
                                val userEmail = snapshot.child("userEmail").value.toString()
                                val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
                                val token = snapshot.child("token").value.toString()
                                val user = User(userName, userEmail, profileImageUrl, token)
                                allFriends.add(user)
                                friendsAdapter.updateList(allFriends)

                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchFriendsRequestsFromDatabase() {
        databaseFriendsRequestsRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    if (ds.value.toString() == "{request=received}") {
                        val userFriendRequestId = ds.key.toString()
                        val databaseFriendsRef = FirebaseDatabase.getInstance().getReference("users/${userFriendRequestId}")

                        databaseFriendsRef.addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userName = snapshot.child("userName").value.toString()
                                val userEmail = snapshot.child("userEmail").value.toString()
                                val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
                                val token = snapshot.child("token").value.toString()
                                val user = User(userName, userEmail, profileImageUrl, token)
                                allFriendsRequests.add(user)
                                friendsRequestsAdapter.updateList(allFriendsRequests)
                                checkIfIsAnyFriendRequest()
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onProfileFriendRequest(userEmail: String) {
        val intent = Intent(this, FriendProfileActivity::class.java)
        intent.putExtra("user", userEmail)
        startActivity(intent)
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<User>()
            for (i in allFriends) {
                if (i.userName.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (allFriends.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                friendsAdapter.setFilteredList(filteredList)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupBottomNavigationBar() {
        binding.bnvHome.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bnv_home -> startMainActivity()
                R.id.bnv_list -> startSavedAdvicesActivity()
                R.id.bnv_add -> startAddFriendActivity()
                else -> {}
            }
            true
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startMainActivity() {
        if (isNetworkAvailable()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showBottomSheetDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startSavedAdvicesActivity() {
        if (isNetworkAvailable()) {
            val intent = Intent(this, SavedAdvicesActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showBottomSheetDialog()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startAddFriendActivity() {
        if (isNetworkAvailable()) {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            showBottomSheetDialog()
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)

        val sheetBinding: CustomBottomSheetBinding = CustomBottomSheetBinding.inflate(layoutInflater, null, false)
        dialog.setContentView(sheetBinding.root)
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)

        return (capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET))
    }

    private fun checkIfIsAnyFriendRequest() {
        if (allFriendsRequests.isEmpty()) {
            binding.rvFriendsRequest.isVisible = false
        }
    }

    override fun onAcceptFriendRequest(userEmail: String) {

        val query: Query = databaseUserRef.orderByChild("userEmail")
            .equalTo(userEmail)
        query.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val userName = ds.child("userName").value.toString()
                    val userEmail = ds.child("userEmail").value.toString()
                    val profileImageUrl = ds.child("profileImageUrl").value.toString()
                    val token = ds.child("token").value.toString()
                    val user = User(userName, userEmail, profileImageUrl, token)
                    val userId = ds.key.toString()
                    auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).child(
                        FriendProfileActivity.STATE_REQUEST
                    ).setValue("friends")
                        databaseRequestRef.child(userId).child(it1).child(FriendProfileActivity.STATE_REQUEST).setValue("friends")
                        databaseUserRef.child(it1).child("Friends").push().setValue(userId)
                        databaseUserRef.child(userId).child("Friends").push().setValue(it1) }
                    allFriendsRequests.remove(user)
                    friendsRequestsAdapter.updateList(allFriendsRequests)
                    allFriends.add(user)
                    friendsAdapter.updateList(allFriends)
                    Toast.makeText(this@FriendsActivity, "Friend Request Accepted", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onRefuseFriendRequest(userEmail: String) {
        val query: Query = databaseUserRef.orderByChild("userEmail")
            .equalTo(userEmail)
        query.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) {
                    val userName = ds.child("userName").value.toString()
                    val userEmail = ds.child("userEmail").value.toString()
                    val profileImageUrl = ds.child("profileImageUrl").value.toString()
                    val token = ds.child("token").value.toString()
                    val user = User(userName, userEmail, profileImageUrl, token)
                    val userId = ds.key.toString()
                    allFriendsRequests.remove(user)
                    friendsRequestsAdapter.updateList(allFriendsRequests)
                    auth.uid?.let { it1 -> databaseRequestRef.child(it1).child(userId).removeValue() }
                    Toast.makeText(this@FriendsActivity, "Friend Request Refused", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onFriendClick(userEmail: String) {
        val intent = Intent(this, FriendProfileActivity::class.java)
        intent.putExtra("user", userEmail)
        startActivity(intent)
    }
}