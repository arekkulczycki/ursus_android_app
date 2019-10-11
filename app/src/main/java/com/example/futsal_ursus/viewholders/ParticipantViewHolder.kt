package com.example.futsal_ursus.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.futsal_ursus.models.data.Participant
import kotlinx.android.extensions.LayoutContainer


class ParticipantViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {

    private lateinit var bound_participant: Participant

    fun bind(participant: Participant) {
        bound_participant = participant
//        full_name.text = "${participant.first_name} ${participant.last_name}"
    }

}
