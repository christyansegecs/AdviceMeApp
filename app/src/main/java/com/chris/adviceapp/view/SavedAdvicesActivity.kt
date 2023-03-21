package com.chris.adviceapp.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.adapter.AdviceListAdapter
import com.chris.adviceapp.adapter.NoteClickDeleteInterface
import com.chris.adviceapp.adapter.NoteClickUpdateInterface
import com.chris.adviceapp.databinding.ActivitySavedAdvicesBinding
import com.chris.adviceapp.usermodel.AdviceFirebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class SavedAdvicesActivity : AppCompatActivity(), NoteClickDeleteInterface, NoteClickUpdateInterface {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdviceListAdapter
    private lateinit var binding: ActivitySavedAdvicesBinding
    val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val databaseAdvicesRef = FirebaseDatabase.getInstance().getReference("users/${user?.uid}/Advices")
    val allAdvices = ArrayList<String>()
    val allAdvicesDates = ArrayList<String>()
    lateinit var adviceBeforeUpdating: String
    private lateinit var adviceAfterUpdating: String

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.data != null) {
            val sdf = SimpleDateFormat("MMM dd,yyyy")
            val currentDate: String = sdf.format(Date())

            val adviceUpdated = result.data?.getStringExtra("advice")
            adviceAfterUpdating = adviceUpdated.toString()
            updateAdvice(AdviceFirebase(adviceBeforeUpdating, currentDate))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivitySavedAdvicesBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        this.recyclerView = this.binding.rvAdvice
        this.adapter = AdviceListAdapter(this,this, this)
        this.recyclerView.adapter = this.adapter
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        setupActionBar()
        fetchAdvicesFromDatabase()
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

    override fun onDeleteIconClick(advice: AdviceFirebase) {

        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_delete_advice))
            .setPositiveButton(getString(R.string.alert_dialog_logout_positive)) { _, _ ->

                val query: Query = databaseAdvicesRef.orderByChild("advice")
                    .equalTo(advice.advice)
                query.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (appleSnapshot in dataSnapshot.children) {
                            appleSnapshot.ref.removeValue()
                            allAdvices.remove(advice.advice)
                            adapter.updateList(allAdvices)
                            allAdvicesDates.remove(advice.date)
                            adapter.updateDateList(allAdvicesDates)
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
            }
            .setNegativeButton(getString(R.string.alert_dialog_logout_negative)) { _, _ ->
            }.create()
        dialog.show()
    }

    override fun onUpdateClick(advice: AdviceFirebase) {
        adviceBeforeUpdating = advice.advice

        val intent = Intent(this, UpdateAdviceActivity::class.java)
        intent.putExtra("adviceSending", advice.advice)
        getResult.launch(intent)
    }

    private fun updateAdvice(advice: AdviceFirebase) {

        val adviceUpdated = mapOf(
            "advice" to adviceAfterUpdating,
        )

        val query: Query = databaseAdvicesRef.orderByChild("advice")
            .equalTo(advice.advice)
        query.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (appleSnapshot in dataSnapshot.children) {

                    appleSnapshot.ref.updateChildren(adviceUpdated)
                    allAdvices.remove(advice.advice)
                    allAdvicesDates.remove(advice.date)
                    allAdvices.add(adviceAfterUpdating)
                    allAdvicesDates.add(advice.date)
                    adapter.updateList(allAdvices)
                    adapter.updateDateList(allAdvicesDates)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
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
                databaseAdvicesRef.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        databaseAdvicesRef.removeValue()
                        allAdvices.clear()
                        adapter.updateList(allAdvices)
                        allAdvicesDates.clear()
                        adapter.updateDateList(allAdvicesDates)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
                Toast.makeText(this,getString(R.string.toast_delete_all), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.alert_dialog_logout_negative)) { _, _ ->
            }.create()
        dialog.show()
    }
}