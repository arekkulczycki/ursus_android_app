package co.techinsports.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import co.techinsports.futsal_ursus.AppSettings
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.models.data.Participant
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.top_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class SettingsFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_settings
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        save_button.isEnabled = false
        top_bar_settings_button.visibility = View.GONE
        logout_button.setOnClickListener {
            logout()
        }
        save_button.setOnClickListener {
            saveData()
        }

        if (prefs.active_group_id > 0)
            syncData()
    }

    private fun logout(){
        prefs.login_token = null
        findNavController().navigate(R.id.action_logout)
    }

    private fun syncData() {
        val url = AppSettings.getUrl("/profile/${prefs.active_group_id}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Participant>
            if (it.count() > 0) {
                if (first_name != null && last_name != null && phone != null) {
                    first_name.setText(it.first().first_name)
                    last_name.setText(it.first().last_name)
                    phone.setText(it.first().phone)
                    save_button.isEnabled = true
                }
            }
        }, deserializer = Participant.Deserializer())
    }

    private fun saveData() {
        if (prefs.active_group_id == 0)
            Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()
        else {
            val url = AppSettings.getUrl("/profile/")
            val body =
                mapOf("first_name" to first_name.text.toString(),
                    "last_name" to last_name.text.toString(),
                    "phone" to phone.text.toString(),
                    "group_id" to prefs.active_group_id)
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
        findNavController().navigate(R.id.action_logout)
        Toast.makeText(context, getString(R.string.token_expired), Toast.LENGTH_SHORT)
            .show()
        super.onUnauthorizedEvent(event)
    }
}