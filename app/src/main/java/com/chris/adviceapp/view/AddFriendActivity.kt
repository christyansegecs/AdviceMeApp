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
import com.chris.adviceapp.adapter.UserClickInterface
import com.chris.adviceapp.adapter.UsersAdapter
import com.chris.adviceapp.databinding.ActivityAddFriendBinding
import com.chris.adviceapp.databinding.CustomBottomSheetBinding
import com.chris.adviceapp.usermodel.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class AddFriendActivity : AppCompatActivity(), UserClickInterface {

    private lateinit var binding: ActivityAddFriendBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    val auth = FirebaseAuth.getInstance()
    private val databaseAdvicesRef = FirebaseDatabase.getInstance().getReference("users")
    val allUsers = ArrayList<User>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityAddFriendBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.recyclerView = this.binding.rvAddFriends
        this.adapter = UsersAdapter(this, this)
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        fetchUsersFromDatabase()
        setupBottomNavigationBar()

        binding.svAddFriends.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })
    }

    private fun fetchUsersFromDatabase() {
        databaseAdvicesRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    val userName = ds.child("userName").value.toString()
                    val userEmail = ds.child("userEmail").value.toString()
                    val profileImageUrl = ds.child("profileImageUrl").value.toString()
                    val user = User(userName, userEmail, profileImageUrl)
                    allUsers.add(user)
                    adapter.updateList(allUsers)
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
            for (i in allUsers) {
                if (i.userName.toLowerCase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (allUsers.isEmpty()) {
                Toast.makeText(this, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupBottomNavigationBar() {
        binding.bnvHome.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bnv_home -> startMainActivity()
                R.id.bnv_list -> startSavedAdvicesActivity()
                R.id.bnv_friends -> startFriendsActivity()
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
    private fun startFriendsActivity() {
        if (isNetworkAvailable()) {
            val intent = Intent(this, FriendsActivity::class.java)
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}