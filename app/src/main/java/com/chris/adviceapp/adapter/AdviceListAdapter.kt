package com.chris.adviceapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chris.adviceapp.R
import com.chris.adviceapp.database.models.Advice

class AdviceListAdapter : ListAdapter<Advice, AdviceListAdapter.AdviceViewHolder>(AdvicesComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdviceViewHolder {
        return AdviceViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: AdviceViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind("${current.id} - ${current.advice}")
    }

    class AdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val adviceItemView: TextView = itemView.findViewById(R.id.tvAdviceDaRV)

        fun bind(text: String?) {
            adviceItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): AdviceViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_advices, parent, false)
                return AdviceViewHolder(view)
            }
        }
    }

    class AdvicesComparator : DiffUtil.ItemCallback<Advice>() {
        override fun areItemsTheSame(oldItem: Advice, newItem: Advice): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Advice, newItem: Advice): Boolean {
            return oldItem.advice == newItem.advice
        }
    }
}