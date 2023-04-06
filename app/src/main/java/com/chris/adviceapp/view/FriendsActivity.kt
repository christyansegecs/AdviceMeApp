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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.adapter.FriendClickInterface
import com.chris.adviceapp.adapter.FriendsAdapter
import com.chris.adviceapp.databinding.ActivityFriendsBinding
import com.chris.adviceapp.databinding.CustomBottomSheetBinding
import com.chris.adviceapp.usermodel.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class FriendsActivity : AppCompatActivity(), FriendClickInterface {

    private lateinit var binding: ActivityFriendsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FriendsAdapter
    val allFriends = ArrayList<User>()
    val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseAdvicesRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/Friends")

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityFriendsBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.recyclerView = this.binding.rvFriends
        this.adapter = FriendsAdapter(this, this)
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        fetchFriendsFromDatabase()

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
        databaseAdvicesRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {

                    val userFriendId = ds.value.toString()
                    val databaseFriendsRef = FirebaseDatabase.getInstance().getReference("users/${userFriendId}")

                    databaseFriendsRef.addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                                val userName = snapshot.child("userName").value.toString()
                                val userEmail = snapshot.child("userEmail").value.toString()
                                val profileImageUrl = snapshot.child("profileImageUrl").value.toString()
                                val token = snapshot.child("token").value.toString()
                                val user = User(userName, userEmail, profileImageUrl, token)
                                allFriends.add(user)
                                adapter.updateList(allFriends)

                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onUserClick(userEmail: String) {
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
                adapter.setFilteredList(filteredList)
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
}