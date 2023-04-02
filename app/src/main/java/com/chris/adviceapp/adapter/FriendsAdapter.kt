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
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.CropCircleTransformation

class FriendsAdapter(
    val context: Context,
    private val friendClickInterface: FriendClickInterface
) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {

    private var allFriends = ArrayList<User>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tvFriends = itemView.findViewById<TextView>(R.id.tvFriends)
        val ivUser = itemView.findViewById<ImageView>(R.id.ivUser)
        val tvUser = itemView.findViewById<TextView>(R.id.tvUserEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_friends,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvFriends.text = allFriends[position].userName
        Picasso.get().load(allFriends[position].profileImageUrl).transform(CropCircleTransformation()).into(holder.ivUser)
        holder.tvUser.text = allFriends[position].userEmail
        holder.tvFriends.setOnClickListener {
            friendClickInterface.onUserClick(allFriends[position].userEmail)
        }
    }

    override fun getItemCount(): Int {
        return allFriends.size
    }

    fun updateList(newList: ArrayList<User>){
        allFriends.clear()
        allFriends.addAll(newList)
        notifyDataSetChanged()
    }

    fun setFilteredList(allUsers: ArrayList<User>){
        this.allFriends = allUsers
        notifyDataSetChanged()
    }

}

interface FriendClickInterface {
    fun onUserClick(userEmail : String)
}