package com.chris.adviceapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.api.AdviceService
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.databinding.ActivityMainBinding
import com.chris.adviceapp.repository.AdviceRepository
import com.chris.adviceapp.util.AdviceState
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.AdviceViewModelFactory
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val retrofitService = AdviceService.getInstance()
    private lateinit var viewModel: AdviceViewModel
    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }
    private lateinit var currentAdvice : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setupActionBar()
        setupViewModel()
        viewModel.getAdvice()
        handleAdvices()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAdvice()

        binding.btnNewAdvice.setOnClickListener { viewModel.getAdvice() }

        binding.btnSaveAdvice.setOnClickListener {
            viewModelDB.insert(Advice(currentAdvice))
        }

        binding.btnList.setOnClickListener {
            val intent = Intent(this, SavedAdvicesActivity::class.java)
            startActivity(intent)
        }
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
        viewModel = ViewModelProvider(this, AdviceViewModelFactory(AdviceRepository(retrofitService))).get(
            AdviceViewModel::class.java
        )
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
                }
                return true
            }
        })
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