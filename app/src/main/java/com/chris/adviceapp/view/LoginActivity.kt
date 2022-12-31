package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setButtonClickListener()
        registerActivityForGoogleSignIn()
    }

    private fun loginWithFirebase(userEmail: String, password: String) {
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext, "Login is successful",
                        Toast.LENGTH_LONG
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
            }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        signIn()
    }

    fun signIn () {
        val signInIntent: Intent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)
    }

    private fun registerActivityForGoogleSignIn() {
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val data = result.data

                if (resultCode == RESULT_OK && data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleSignIn.getSignedInAccountFromIntent(data)
                    firebaseSignInWithGoogle(task)
                } else {
                    Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account : GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, "Welcome", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            firebaseGoogleAccount(account)
            finish()
        } catch (e : ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener(this) {
                task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
            } else {
                Toast.makeText(
                    applicationContext, "Login error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setButtonClickListener() {

        binding.btnLogin.setOnClickListener {
            val userEmail = binding.tvEmail.text.toString()
            val password = binding.tvPassword.text.toString()
            loginWithFirebase(userEmail, password)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.btnForget.setOnClickListener {
            val intent = Intent(this, ForgetActivity::class.java)
            startActivity(intent)
        }

        binding.btnPhone.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener{
            signInGoogle()
        }
    }

}