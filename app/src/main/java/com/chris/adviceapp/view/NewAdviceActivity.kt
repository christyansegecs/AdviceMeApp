package com.chris.adviceapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chris.adviceapp.R
import com.chris.adviceapp.databinding.ActivityNewAdviceBinding
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class NewAdviceActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityNewAdviceBinding
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseRef = FirebaseDatabase.getInstance().getReference("/users/${user?.uid}")
//    private val viewModelDB: AdviceDatabaseViewModel by viewModels {
//        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityNewAdviceBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        val newAdvice = binding.tvNewAdvice.text

        binding.btnNewAdvice.setOnClickListener {
            val sdf = SimpleDateFormat("MMM dd,yyyy")
            val currentDate: String = sdf.format(Date())
//            viewModelDB.insert(Advice(newAdvice.toString(), currentDate))
            databaseRef.child("Advices").push().setValue(AdviceFirebase(newAdvice.toString(), currentDate))
            Toast.makeText(this, getString(R.string.toast_new_advice) , Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}