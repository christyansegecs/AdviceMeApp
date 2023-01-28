package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.databinding.ActivityMainBinding
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.util.AdviceState
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.AdviceViewModelFactory
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val retrofitService = AdviceService.getInstance()
    private lateinit var viewModel: AdviceViewModel
    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }
    private lateinit var currentAdvice : String
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseRef = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setupActionBar()
        setupViewModel()
        viewModel.getAdvice()
        handleAdvices()
        setupClickListener()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAdvice()
    }

    private fun handleAdvices() = lifecycleScope.launchWhenCreated{
        viewModel._adviceStateFlow.collect {
            when(it) {
                is AdviceState.onLoading -> { binding.progressBar.isVisible=true }
                is AdviceState.onError -> {
                    AdviceState.onError(error(getString(R.string.error_handling_advices)))
                    binding.progressBar.isVisible = false }
                is AdviceState.onSuccess -> {
                    it.data.body()?.slip?.advice
                    binding.tvAdvice.text = it.data.body()?.slip?.advice
                    currentAdvice = it.data.body()?.slip?.advice.toString()
                    binding.progressBar.isVisible=false
                    }
                is AdviceState.Empty -> {}
            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, AdviceViewModelFactory(AdviceRepository(retrofitService)))[AdviceViewModel::class.java]
    }

    private fun setupActionBar() {
        addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_home, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.signOut -> {
                        showDialog()
                    }
                    R.id.userProfile -> {
                        goToUserProfile()
                    }
                }
                return true
            }
        })
    }

    private fun goToUserProfile() {
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
    }

    private fun setupClickListener() {
        binding.btnNewAdvice.setOnClickListener { viewModel.getAdvice() }

        binding.btnSaveAdvice.setOnClickListener {
            val sdf = SimpleDateFormat("MMM dd,yyyy")
            val currentDate: String = sdf.format(Date())
//            viewModelDB.insert(Advice(currentAdvice, currentDate))
            val adviceSaved = AdviceFirebase(currentAdvice, currentDate)
            databaseRef.child("Advices").push().setValue(adviceSaved)
        }

        binding.btnList.setOnClickListener {
            val intent = Intent(this, SavedAdvicesActivity::class.java)
            startActivity(intent)
        }
        binding.fabNewAdvice.setOnClickListener {
            val intent = Intent(this, NewAdviceActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showDialog() {

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_dialog_logout))
            .setMessage(getString(R.string.alert_dialog_logout_question))
            .setPositiveButton(getString(R.string.alert_dialog_logout_positive)) { _, _ ->

                // sign ou for email and password
                FirebaseAuth.getInstance().signOut()

                // sign ou for facebook
                LoginManager.getInstance().logOut()

                //  sign out for google account
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail().build()
                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(applicationContext, getString(R.string.alert_dialog_logout_successful), Toast.LENGTH_SHORT).show()
                    }
                }

                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton(getString(R.string.alert_dialog_logout_negative)) { _, _ ->
            }.create()
        dialog.show()
    }

}