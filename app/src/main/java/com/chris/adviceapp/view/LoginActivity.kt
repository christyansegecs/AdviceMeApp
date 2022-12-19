package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.chris.adviceapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        binding.btnLogin.setOnClickListener {
            val userEmail = binding.userEmail.text.toString()
            val password = binding.password.text.toString()
            loginWithFirebase(userEmail, password)
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.forgotpass.setOnClickListener {
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

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("810371366875-ruatceu4ca7sn3v16h08j6rr0iia7gvt.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    private fun loginWithFirebase(userEmail: String, password: String) {
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

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInGoogle() {
        intent = googleSignInClient.signInIntent
        startActivityForResult(intent, 1)
    }


    private fun loginGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token,null)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext, "Login efetuado com sucesso",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    applicationContext, "Erro ao efetuar login",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 1) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(intent).result
            try {
                account.idToken?.let { loginGoogle(it) }
            } catch (exception : ApiException) {
                Toast.makeText(
                    applicationContext, "Erro ao efetuar login, nenhum usuário Google cadastrado no dispositivo",
                    Toast.LENGTH_LONG
                ).show()
            }
            finally {
                Toast.makeText(
                    applicationContext, "Erro ao efetuar login, nenhum usuário Google cadastrado no dispositivo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}