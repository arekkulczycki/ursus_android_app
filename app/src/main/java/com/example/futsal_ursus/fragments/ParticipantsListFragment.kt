package com.example.futsal_ursus.fragments

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.adapters.ParticipantRecyclerViewAdapter
import com.example.futsal_ursus.models.data.Participant
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_participants_list.*
import kotlinx.android.synthetic.main.top_bar.*

class ParticipantsListFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_participants_list
    override fun eventBusEnabled(): Boolean = true
    private val args: ParticipantsListFragmentArgs by navArgs()

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            findNavController().navigate(R.id.action_logout)
        top_bar_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }

        val url = AppSettings.getUrl("/user/team/${args.groupId}/${args.eventId}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Participant>
            syncParticipantsList(it)
        }, deserializer=Participant.Deserializer())
    }

    fun syncParticipantsList(participants: List<Participant>){
        participants_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ParticipantRecyclerViewAdapter(participants)
        }
    }
}