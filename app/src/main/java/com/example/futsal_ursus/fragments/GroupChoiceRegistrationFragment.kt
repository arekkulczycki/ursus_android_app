package com.example.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Credentials
import com.example.futsal_ursus.models.data.Group
import com.example.futsal_ursus.models.events.RegistrationEvent
import com.example.futsal_ursus.models.events.ServerErrorEvent
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_group_choice_registration.*
import kotlinx.android.synthetic.main.fragment_login.register_button
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GroupChoiceRegistrationFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_group_choice_registration
    override fun eventBusEnabled(): Boolean = true

    var chosen_group_id: Int? = null

    override fun initFragment(view: View) {
        syncGroups()
        register_button.setOnClickListener {
            register()
        }
    }

    private fun register() {
        register_button.isEnabled = false
        if (validate()) {
            val username = register_username.text.toString()
            val password = register_password.text.toString()
            val credentials = Credentials(username, password, chosen_group_id)
            val url: String = AppSettings.getUrl("/user/register/")
            APIRequest().post(url, credentials, {
                @Suppress("UNCHECKED_CAST")
                it as RegistrationEvent
                EventBus.getDefault().post(it)
            }, deserializer = RegistrationEvent.Deserializer())
        } else
            register_button.isEnabled = true
    }

    private fun validate(): Boolean {
        var isValid = true
        if (register_password.text == null || register_password.text.isEmpty()) {
            register_password.error = getString(R.string.field_required)
            isValid = false
        }
        if (register_confirm_password.text == null || register_confirm_password.text.isEmpty()) {
            register_confirm_password.error = getString(R.string.field_required)
            isValid = false
        }
        if (register_password.text.toString() != register_confirm_password.text.toString()) {
            register_password.error = getString(R.string.field_required)
            register_confirm_password.error = getString(R.string.field_required)
            isValid = false
        }
        return isValid
    }

    private fun syncGroups() {
        //TODO: wybór grupy do której dołącza user
        val url: String = AppSettings.getUrl("/groups/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Group>
            if (!it.isEmpty())
                chosen_group_id = it.first().id
        }, deserializer = Group.Deserializer())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRegisterEvent(event: RegistrationEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        register_button.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
            val attended_groups = event.attended_groups
            prefs.attended_groups = attended_groups
            prefs.active_group_id = attended_groups.first()
            Toast.makeText(context, getString(R.string.login_successful_text), Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_groupChoiceRegistrationFragment_to_mainPageFragment, null)
        } else if (event.reason == 0){
            Toast.makeText(context, getString(R.string.login_unsuccessful_text), Toast.LENGTH_SHORT)
                .show()
        } else if (event.reason == 1)
            Toast.makeText(context, getString(R.string.login_unsuccessful_text), Toast.LENGTH_SHORT)
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        register_button.isEnabled = true
        super.onServerErrorEvent(event)
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupChoiceRegistrationFragment()
    }
}