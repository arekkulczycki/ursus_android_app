package co.techinsports.futsal_ursus.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.models.data.Participant

class ParticipantRecyclerViewAdapter(private val itemList: List<Participant>) :
    RecyclerView.Adapter<ParticipantRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        val participantNumber: TextView = view.findViewById(R.id.participant_number)
        val participantFullName: TextView = view.findViewById(R.id.participant_full_name)
        val layout: TextView = view.findViewById(R.id.participant_row)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_participant, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item?.present == false)
            viewHolder.layout.setBackgroundColor(0xA8FF0000.toInt())
        viewHolder.participantNumber.text = (position + 1).toString()
        if (item?.first_name.isNullOrEmpty() && item?.last_name.isNullOrEmpty())
            viewHolder.participantFullName.text = "${item?.email}... (brak danych)"
        else
            viewHolder.participantFullName.text = "${item?.first_name} ${item?.last_name}"
    }

    private fun getItem(position: Int): Participant? {
        return if (position < itemList.size) itemList.get(position) else null
    }

    override fun getItemCount(): Int = itemList.size
}
