package com.example.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Credentials
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

    override fun initFragment(view: View) {
        register_button.setOnClickListener {
            register()
        }
    }

    private fun register() {
        register_button.isEnabled = false
        if (validate()) {
            val username = register_username.text.toString()
            val password = register_password.text.toString()
            val credentials = Credentials(username, password)
            val url: String = AppSettings.getUrl("/user/register/")
            APIRequest().post(url, credentials, {
                @Suppress("UNCHECKED_CAST")
                it as Map<String, String>?
                val reason = it?.getOrDefault("reason", "0")!!.toInt()
                val token = it?.getOrDefault("token", "")
                if (token != null && !token.isEmpty())
                    EventBus.getDefault().post(RegistrationEvent(true, reason, token))
                else
                    EventBus.getDefault().post(RegistrationEvent(false, reason, token))
            })
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRegisterEvent(event: RegistrationEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        register_button.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
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
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupChoiceRegistrationFragment()
    }
}