package com.example.futsal_ursus.fragments

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futsal_ursus.R
import com.example.futsal_ursus.adapters.ParticipantRecyclerViewAdapter
import com.example.futsal_ursus.models.data.Participant
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_participants_list.*

class ParticipantsListFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_participants_list
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        val participants = emptyList<Participant>()
        participants_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParticipantRecyclerViewAdapter(participants)
        }
    }
}