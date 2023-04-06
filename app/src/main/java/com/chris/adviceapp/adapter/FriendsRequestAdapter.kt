package com.chris.adviceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.chris.adviceapp.R
import com.chris.adviceapp.usermodel.User

class FriendsRequestAdapter(
    val context: Context,
    private val friendRequestAcceptInterface: FriendRequestAcceptInterface,
    private val friendRequestRefuseInterface: FriendRequestRefuseInterface,
    private val friendRequestProfileInterface: FriendRequestProfileInterface
) : RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>() {

    private var allFriendsRequests = ArrayList<User>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val ivFriendRequest = itemView.findViewById<ImageView>(R.id.ivFriendRequest)
        val tvNameFriendRequest = itemView.findViewById<TextView>(R.id.tvNameFriendRequest)
        val btnAcceptFriendRequest = itemView.findViewById<Button>(R.id.btnAccept)
        val btnRefuseFriendRequest = itemView.findViewById<Button>(R.id.btnRefuse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsRequestAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_request,parent,false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(allFriendsRequests[position].profileImageUrl).apply(
            RequestOptions().transform(
                CircleCrop()
            )).into((holder.ivFriendRequest))
        holder.tvNameFriendRequest.text = allFriendsRequests[position].userName
        holder.tvNameFriendRequest.setOnClickListener {
            friendRequestProfileInterface.onProfileFriendRequest(allFriendsRequests[position].userEmail)
        }
        holder.btnAcceptFriendRequest.setOnClickListener {
            friendRequestAcceptInterface.onAcceptFriendRequest(allFriendsRequests[position].userEmail)
        }
        holder.btnRefuseFriendRequest.setOnClickListener {
            friendRequestRefuseInterface.onRefuseFriendRequest(allFriendsRequests[position].userEmail)
        }
    }

    override fun getItemCount(): Int {
        return allFriendsRequests.size
    }

    fun updateList(newList: ArrayList<User>){
        allFriendsRequests.clear()
        allFriendsRequests.addAll(newList)
        notifyDataSetChanged()
    }
}

interface FriendRequestAcceptInterface {
    fun onAcceptFriendRequest(userEmail: String)
}

interface FriendRequestRefuseInterface {
    fun onRefuseFriendRequest(userEmail: String)
}

interface FriendRequestProfileInterface {
    fun onProfileFriendRequest(userEmail: String)
}