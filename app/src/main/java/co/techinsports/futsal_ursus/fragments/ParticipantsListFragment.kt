package co.techinsports.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import co.techinsports.futsal_ursus.AppSettings
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.adapters.ParticipantRecyclerViewAdapter
import co.techinsports.futsal_ursus.databinding.FragmentParticipantsListBinding
import co.techinsports.futsal_ursus.models.data.Participant
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ParticipantsListFragment :
    BaseFragment<FragmentParticipantsListBinding>(FragmentParticipantsListBinding::inflate) {
    override fun eventBusEnabled(): Boolean = true
    private val args: ParticipantsListFragmentArgs by navArgs()
    var participants: List<Participant> = emptyList()

    private val binding get() = viewBinding as FragmentParticipantsListBinding

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            getNavController().navigate(R.id.action_logout)
        binding.participantsTopBar.topBarSettingsButton.setOnClickListener {
            getNavController().navigate(R.id.action_global_settingsFragment)
        }
        //TODO: zrobić poniższe prawidłowo
        binding.teamTitle.text =
            args.eventTitle.replace("match", "mecz").replace("training", "trening")

        val url = AppSettings.getUrl("/team/${args.eventId}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Participant>
            participants = it
            for (i in 1 until 50)
                if (binding.participantsRecyclerView != null)
                    break
            Thread.sleep(10)
            initParticipantsList()
        }, deserializer = Participant.Deserializer())
    }

    // inicjalizuje tylko wtedy jeśli ma dane (zmieniły się dane?)
    private fun initParticipantsList() {
        binding.participantsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ParticipantRecyclerViewAdapter(participants)
        }
    }

    // być może się przyda do odświeżania widoku w przyszłości
    private fun syncParticipantsList() {
        binding.participantsRecyclerView.adapter?.notifyDataSetChanged()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        super.onServerErrorEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        getNavController().navigate(R.id.action_logout)
        Toast.makeText(context, getString(R.string.token_expired), Toast.LENGTH_SHORT)
            .show()
        super.onUnauthorizedEvent(event)
    }
}