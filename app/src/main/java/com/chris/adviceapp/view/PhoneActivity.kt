package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.databinding.ActivityPhoneBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.Locale
import java.util.concurrent.TimeUnit

class PhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var callbacks : PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var verificationCode : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val country = Locale.getDefault().country
        binding.tvPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher(country))
        setButtonClickListeners()
        authenticationSettings()
    }

    private fun authenticationSettings() {
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.d("PhoneAuth", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("PhoneAuth", "onVerificationFailed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(verificationId, token)
                Log.d("PhoneAuth", "CodeSent:${verificationId}")
                verificationCode = verificationId
            }
        }
    }

    private fun sendCodeToPhoneNumber(userPhoneNumber: String) {

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(userPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
        auth.useAppLanguage()
    }

    private fun signInWithSMSCode(code: String) {

        val credential = PhoneAuthProvider.getCredential(verificationCode, code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                Log.d("PhoneAuth", "Success: signInWithCredential")
                finish()
            } else {
                Log.d("PhoneAuth", "Failed: signInWithCredential. ${task.exception.toString()}")
                Toast.makeText(applicationContext, "The code you entered is incorrect",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setButtonClickListeners() {

        binding.btnSms.setOnClickListener {

            val userPhoneNumber = binding.tvPhone.text.toString()

            if (userPhoneNumber.isEmpty()) {
                binding.tvPhone.error = "Input Your Phone Number"
                binding.tvPhone.requestFocus()
            } else {
                sendCodeToPhoneNumber(BrazilPrefixPhoneNumber + userPhoneNumber)
            }
        }

        binding.btnSmsVerify.setOnClickListener {

            val code = binding.tvPhoneVerify.text.toString()

            if (code.isEmpty()) {
                binding.tvPhoneVerify.error = "Input the code you've received"
                binding.tvPhoneVerify.requestFocus()
            } else {
                signInWithSMSCode(code)
            }
        }
    }

    companion object {
        const val BrazilPrefixPhoneNumber = "+55"
    }
}