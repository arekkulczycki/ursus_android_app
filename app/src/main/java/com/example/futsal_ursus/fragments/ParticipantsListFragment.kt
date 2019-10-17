package com.example.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.adapters.ParticipantRecyclerViewAdapter
import com.example.futsal_ursus.models.data.Participant
import com.example.futsal_ursus.models.events.UnauthorizedEvent
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_participants_list.*
import kotlinx.android.synthetic.main.top_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ParticipantsListFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_participants_list
    override fun eventBusEnabled(): Boolean = true
    private val args: ParticipantsListFragmentArgs by navArgs()
    var participants: List<Participant> = emptyList()

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            findNavController().navigate(R.id.action_logout)
        top_bar_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }

        val url = AppSettings.getUrl("/team/${args.eventId}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Participant>
            participants = it
            initParticipantsList()
        }, deserializer=Participant.Deserializer())
    }

    // inicjalizuje tylko wtedy jeśli ma dane (zmieniły się dane?)
    private fun initParticipantsList(){
        participants_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ParticipantRecyclerViewAdapter(participants)
        }
    }

    // być może się przyda do odświeżania widoku w przyszłości
    private fun syncParticipantsList(){
        participants_recycler_view.adapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        findNavController().navigate(R.id.action_logout)
        Toast.makeText(context, getString(R.string.token_expired), Toast.LENGTH_SHORT)
            .show()
        super.onUnauthorizedEvent(event)
    }
}