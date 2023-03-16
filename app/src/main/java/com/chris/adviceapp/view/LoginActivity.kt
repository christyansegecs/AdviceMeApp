package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityLoginBinding
import com.chris.adviceapp.util.AuthState
import com.chris.adviceapp.viewmodel.FirebaseViewModel
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Arrays

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val firebaseViewModel : FirebaseViewModel by viewModel()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var callbackManager: CallbackManager? = null
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        this.binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        setButtonClickListener()
        registerActivityForGoogleSignIn()
        handleAuthentication()
    }

    override fun onStart() {
        super.onStart()
        firebaseViewModel.currentUserLiveData.observe(this) { user ->
            if (user != null) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        firebaseViewModel.getCurrentUser()
    }

    private fun signIn(userEmail: String, password: String) {
        firebaseViewModel.signIn(userEmail, password)
    }

    private fun handleAuthentication() = lifecycleScope.launchWhenCreated{
        firebaseViewModel._authStateFlow.collect {
            when(it) {
                is AuthState.onLoading -> {  }
                is AuthState.onError -> { Toast.makeText(
                    applicationContext, getString(R.string.login_failed),
                    Toast.LENGTH_LONG
                ).show() }
                is AuthState.onSuccess -> { val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                    applicationContext, getString(R.string.login_successful),
                    Toast.LENGTH_LONG
                ).show() }
                is AuthState.Empty -> { }
            }
        }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        signInWithGoogle()
    }

    private fun signInWithGoogle() {
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
                    Toast.makeText(applicationContext, getString(R.string.login_google_failed), Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(applicationContext, getString(R.string.login_google_successful), Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            firebaseGoogleAccount(account)
            finish()
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val auth : FirebaseAuth = FirebaseAuth.getInstance()
        val authCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(authCredential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
            } else {
                Toast.makeText(
                    applicationContext, getString(R.string.login_google_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun loginWithFacebook() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .logInWithReadPermissions(this, Arrays.asList("public_profile", "email"))
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d("FacebookAuthentication", "Facebook token: " + loginResult.accessToken.token)
                    handleFacebookAccessToken(loginResult!!.accessToken)
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(
                        applicationContext, getString(R.string.login_facebook_successful),
                        Toast.LENGTH_LONG
                    ).show()}
                override fun onCancel() {
                    Toast.makeText(
                        applicationContext, getString(R.string.login_facebook_canceled),
                        Toast.LENGTH_LONG
                    ).show()}
                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        applicationContext, getString(R.string.login_facebook_failed),
                        Toast.LENGTH_LONG
                    ).show()}
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        val credential: AuthCredential = FacebookAuthProvider.getCredential(accessToken!!.token)
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
            }
        }
    }

    private fun loginWithTwitter() {
        val provider = OAuthProvider.newBuilder("twitter.com")
        provider.addCustomParameter("lang", "fr")
        auth
            .startActivityForSignInWithProvider(this, provider.build())
            .addOnSuccessListener { authResult ->
                Toast.makeText(
                    this,
                    getString(R.string.login_twitter_successful)+ authResult.user!!.displayName,
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("Twitter", "addOnSuccessListener")
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, getString(R.string.login_twitter_failed) + e.message, Toast.LENGTH_SHORT).show()
                Log.d("Twitter", e.message.toString())
            }
    }

    private fun setButtonClickListener() {

        binding.btnLogin.setOnClickListener {

            val userEmail = binding.tvEmail.text.toString()
            val password = binding.tvPassword.text.toString()

            if (userEmail.isEmpty()) {
                binding.tvEmail.error = getString(R.string.error_input_your_email)
                binding.tvEmail.requestFocus()
            } else if (password.isEmpty()) {
                binding.tvPassword.error = getString(R.string.error_input_your_password)
                binding.tvPassword.requestFocus()
            } else {
                signIn(userEmail, password)
            }
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

        binding.btnGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.btnFacebook.setOnClickListener {
            loginWithFacebook()
        }

        binding.btnTwitter.setOnClickListener {
            loginWithTwitter()
        }

        binding.icRedEye.setOnClickListener{
            if (binding.tvPassword.transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                binding.tvPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_hide)
            } else {
                binding.tvPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.icRedEye.setImageResource(R.drawable.ic_remove_red_eye)
            }
        }
    }
}