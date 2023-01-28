package com.chris.adviceapp.view

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.AdviceApplication
import com.chris.adviceapp.R
import com.chris.adviceapp.adapter.AdviceListAdapter
import com.chris.adviceapp.adapter.NoteClickDeleteInterface
import com.chris.adviceapp.database.models.Advice
import com.chris.adviceapp.databinding.ActivitySavedAdvicesBinding
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModel
import com.chris.adviceapp.viewmodel.AdviceDatabaseViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SavedAdvicesActivity : AppCompatActivity(), NoteClickDeleteInterface {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdviceListAdapter
    private lateinit var binding: ActivitySavedAdvicesBinding
    private val adviceDatabaseViewModel : AdviceDatabaseViewModel by viewModels {
        AdviceDatabaseViewModelFactory((application as AdviceApplication).repository)
    }
    val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseAdvicesRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/Advices")
    val allAdvices = ArrayList<String>()
    val allAdvicesDates = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySavedAdvicesBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.recyclerView = this.binding.rvAdvice
        this.adapter = AdviceListAdapter(this,this)
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        setupActionBar()
        fetchAdvicesFromDatabase()
//        adviceDatabaseViewModel.allAdvices.observe(this) { list ->
//            list?.let {
//                adapter.updateList(it)
//            }
//        }
    }

    private fun checkIfIsAnyAdviceSaved() {
        if (allAdvices.isEmpty()) {
            with(binding) {
                rvAdvice.isVisible = false
                tvNoAdvicesYet.isVisible = true
            }
        }
        else {
            with(binding) {
                rvAdvice.isVisible = true
                tvNoAdvicesYet.isVisible = false
            }
        }
    }

    private fun fetchAdvicesFromDatabase() {
        databaseAdvicesRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(ds in snapshot.children) {
                    val advice = ds.child("advice").value.toString()
                    val date = ds.child("date").value.toString()
                    allAdvices.add(advice)
                    adapter.updateList(allAdvices)
                    allAdvicesDates.add(date)
                    adapter.updateDateList(allAdvicesDates)
                }
                checkIfIsAnyAdviceSaved()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDeleteIconClick(advice: Advice) {

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_advice))
            .setPositiveButton(getString(R.string.alert_dialog_logout_positive)) { _, _ ->
                adviceDatabaseViewModel.delete(advice)
                Toast.makeText(this,"${advice.advice} ${getString(R.string.toast_delete_advice)}", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.alert_dialog_logout_negative)) { _, _ ->
            }.create()
        dialog.show()
    }

    private fun setupActionBar() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_saved_advices, menu)
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId) {
                    R.id.menu_delete -> {
                        deleteAll()
                    }
                }
                return true
            }
        })
    }

    fun deleteAll() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_all_advices))
            .setPositiveButton(getString(R.string.alert_dialog_logout_positive)) { _, _ ->
                adviceDatabaseViewModel.deleteAll()
                Toast.makeText(this,getString(R.string.toast_delete_all), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.alert_dialog_logout_negative)) { _, _ ->
            }.create()
        dialog.show()
    }
}