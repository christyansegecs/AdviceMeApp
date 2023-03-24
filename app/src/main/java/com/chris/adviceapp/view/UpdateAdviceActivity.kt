package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.databinding.ActivityUpdateAdviceBinding

class UpdateAdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateAdviceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityUpdateAdviceBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val received = intent.getStringExtra("adviceSending")
        binding.tvUpdateAdvice.setText(received.toString())

        binding.btnConfirmUpdate.setOnClickListener {
            val advice = binding.tvUpdateAdvice.text.toString()

            val intent = Intent()
            intent.putExtra("advice", advice)
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.btnCancelUpdate.setOnClickListener {
            finish()
        }
    }
}