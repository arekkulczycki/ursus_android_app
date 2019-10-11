package com.example.futsal_ursus.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Participant
import com.example.futsal_ursus.viewholders.ParticipantViewHolder

class ParticipantRecyclerViewAdapter(private val itemList: List<Participant>) :
    RecyclerView.Adapter<ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_participant, parent, false)
        return ParticipantViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: ParticipantViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.bind(item!!)
    }

    private fun getItem(position: Int): Participant? {
        return if (position < itemList.size) itemList.get(position) else null
    }

    override fun getItemCount(): Int = itemList.size
}
