package com.chris.adviceapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.chris.adviceapp.databinding.ActivityPhoneBinding

class PhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(this.binding.root)
    }
}