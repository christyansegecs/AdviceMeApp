package com.chris.adviceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.usermodel.User
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class UsersAdapter(
    val context: Context,
    private val userClickInterface: UserClickInterface
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var allUsers = ArrayList<User>()
    val auth = FirebaseAuth.getInstance()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvFriends = itemView.findViewById<TextView>(R.id.tvFriends)
        val ivUser = itemView.findViewById<ImageView>(R.id.ivUser)
        val tvUser = itemView.findViewById<TextView>(R.id.tvUserEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_friends,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFriends.text = allUsers[position].userName
        Picasso.get().load(allUsers[position].profileImageUrl).transform(CropCircleTransformation()).into(holder.ivUser)
        holder.tvUser.text = allUsers[position].userEmail
        holder.tvFriends.setOnClickListener {
            userClickInterface.onUserClick(allUsers[position].userEmail)
        }
    }

    override fun getItemCount(): Int {
        return allUsers.size
    }

    fun updateList(newList: ArrayList<User>){
        allUsers.clear()
        allUsers.addAll(newList)
        notifyDataSetChanged()
    }

    fun setFilteredList(allUsers: ArrayList<User>){
        this.allUsers = allUsers
        notifyDataSetChanged()
    }
}

interface UserClickInterface {
    fun onUserClick(userEmail : String)
}