package com.chris.adviceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.database.models.Advice
import com.google.firebase.auth.FirebaseAuth

class AdviceListAdapter(
    val context: Context,
    private val noteClickDeleteInterface: NoteClickDeleteInterface
) : RecyclerView.Adapter<AdviceListAdapter.ViewHolder>() {

    private val allAdvices = ArrayList<Advice>()
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvAdvice = itemView.findViewById<TextView>(R.id.tvAdviceDaRV)
        val icDelete = itemView.findViewById<ImageView>(R.id.icDelete)
//        val ivUser = itemView.findViewById<ImageView>(R.id.ivUser)
        val tvUser = itemView.findViewById<TextView>(R.id.tvUser)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_advices,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAdvice.text = "${allAdvices[position].id} - ${allAdvices[position].advice}"
        holder.tvDate.text = allAdvices[position].date
        holder.tvUser.text = "${user?.email}"
        holder.icDelete.setOnClickListener{
            noteClickDeleteInterface.onDeleteIconClick(allAdvices[position])
        }
    }

    override fun getItemCount(): Int {
        return allAdvices.size
    }

    fun updateList(newList: List<Advice>){
        allAdvices.clear()
        allAdvices.addAll(newList)
        notifyDataSetChanged()
    }

}

interface  NoteClickDeleteInterface{
    fun onDeleteIconClick(advice: Advice)
}