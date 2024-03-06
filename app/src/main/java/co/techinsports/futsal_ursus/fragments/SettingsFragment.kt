package co.techinsports.futsal_ursus.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.techinsports.futsal_ursus.AppSettings
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.databinding.FragmentSettingsBinding
import co.techinsports.futsal_ursus.models.data.Participant
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {
    override fun eventBusEnabled(): Boolean = true

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initFragment(view: View) {
        binding.saveButton.isEnabled = false
        binding.settingsTopBar.topBarSettingsButton.visibility = View.GONE
        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.saveButton.setOnClickListener {
            saveData()
        }

        if (prefs.active_group_id > 0)
            syncData()
    }

    private fun logout() {
        prefs.login_token = null
        getNavController().navigate(R.id.action_logout)
    }

    private fun syncData() {
        val url = AppSettings.getUrl("/profile/${prefs.active_group_id}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Participant>
            println(it)
            if (it.count() > 0) {
                for (i in 1 until 50)
                    if (binding.firstName != null && binding.lastName != null && binding.phone != null)
                        break
                Thread.sleep(10)
                binding.firstName.setText(it.first().first_name)
                binding.lastName.setText(it.first().last_name)
                binding.phone.setText(it.first().phone)
                binding.saveButton.isEnabled = true
            }
        }, deserializer = Participant.Deserializer())
    }

    private fun saveData() {
        if (prefs.active_group_id == 0)
            Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
        else {
            val url = AppSettings.getUrl("/profile/")
            val body =
                mapOf(
                    "first_name" to binding.firstName.text.toString(),
                    "last_name" to binding.lastName.text.toString(),
                    "phone" to binding.phone.text.toString(),
                    "group_id" to prefs.active_group_id
                )
            APIRequest().post(url, body, {
                Toast.makeText(context, getString(R.string.saved), Toast.LENGTH_SHORT).show()
            })
        }
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