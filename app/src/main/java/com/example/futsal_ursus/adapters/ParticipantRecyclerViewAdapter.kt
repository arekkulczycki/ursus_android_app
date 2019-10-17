package com.example.futsal_ursus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Participant
import kotlinx.android.synthetic.main.row_participant.view.*

class ParticipantRecyclerViewAdapter(private val itemList: List<Participant>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_participant, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        viewHolder.participant_number.text = (position + 1).toString()
        if (item?.first_name.isNullOrEmpty() && item?.last_name.isNullOrEmpty())
            viewHolder.participant_full_name.text = "${item?.email?.take(3)}..."
        else
            viewHolder.participant_full_name.text = "${item?.first_name} ${item?.last_name}"
    }

    private fun getItem(position: Int): Participant? {
        return if (position < itemList.size) itemList.get(position) else null
    }

    override fun getItemCount(): Int = itemList.size
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val participant_number = view.participant_number
    val participant_full_name = view.participant_full_name
}