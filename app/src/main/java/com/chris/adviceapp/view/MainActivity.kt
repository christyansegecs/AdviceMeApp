package com.chris.adviceapp.view

import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityMainBinding
import com.chris.adviceapp.databinding.CustomBottomSheetBinding
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.chris.adviceapp.util.AdviceState
import com.chris.adviceapp.viewmodel.AdviceViewModel
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adviceViewModel : AdviceViewModel by viewModel()
    private val firebaseViewModel : FirebaseViewModel by viewModel()
    private lateinit var currentAdvice : String

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setupActionBar()
        if (isNetworkAvailable()) {
            adviceViewModel.getAdvice()
        } else {
            showBottomSheetDialog()
        }

        handleAdvices()
        setupClickListener()
        firebaseViewModel.getCurrentUser()
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

        return (capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET))
    }

    private fun handleAdvices() = lifecycleScope.launchWhenCreated{
        adviceViewModel._adviceStateFlow.collect {
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

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupClickListener() {
        binding.btnNewAdvice.setOnClickListener { adviceViewModel.getAdvice() }

        binding.btnSaveAdvice.setOnClickListener {

            if (isNetworkAvailable()) {
                val sdf = SimpleDateFormat("MMM dd,yyyy")
                val currentDate: String = sdf.format(Date())
                val adviceSaved = AdviceFirebase(currentAdvice, currentDate)
                firebaseViewModel.getCurrentUser()
                firebaseViewModel.saveAdvice(adviceSaved)

            } else {
                showBottomSheetDialog()
            }

        }

        binding.btnList.setOnClickListener {

            if (isNetworkAvailable()) {
                val intent = Intent(this, SavedAdvicesActivity::class.java)
                startActivity(intent)
            } else {
                showBottomSheetDialog()
            }

        }
        binding.fabNewAdvice.setOnClickListener {

            if (isNetworkAvailable()) {
                val intent = Intent(this, NewAdviceActivity::class.java)
                startActivity(intent)
            }
            else {
                showBottomSheetDialog()
            }
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