package co.techinsports.futsal_ursus.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.models.data.Participant
import kotlinx.android.synthetic.main.fragment_participants_list.view.*
import kotlinx.android.synthetic.main.row_participant.view.*
import kotlinx.android.synthetic.main.row_participant.view.participant_row

class ParticipantRecyclerViewAdapter(private val itemList: List<Participant>) :
    RecyclerView.Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_participant, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item = getItem(position)
        if (item?.present == false)
            viewHolder.layout.setBackgroundColor(0xA8FF0000.toInt())
        viewHolder.participant_number.text = (position + 1).toString()
        if (item?.first_name.isNullOrEmpty() && item?.last_name.isNullOrEmpty())
            viewHolder.participant_full_name.text = "${item?.email}... (brak danych)"
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
    val layout = view.participant_row
}