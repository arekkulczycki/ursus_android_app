package com.example.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.AppSettings.Companion.getUrl
import kotlinx.android.synthetic.main.fragment_login.*

import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Credentials
import com.example.futsal_ursus.models.events.LoginEvent
import com.example.futsal_ursus.models.events.ServerErrorEvent
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoginFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_login
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        login_username.requestFocus()
        login_button.setOnClickListener {
            login()
        }
        register_button.setOnClickListener {
            register()
        }
    }

    private fun login() {
        login_button.isEnabled = false
        if (validate()) {
            val username = login_username.text.toString()
            val password = login_password.text.toString()
            val credentials = Credentials(username, password)
            val url: String = getUrl("/user/token/")
            APIRequest().post(url, credentials, {
                @Suppress("UNCHECKED_CAST")
                it as LoginEvent
                EventBus.getDefault().post(it)
            }, deserializer = LoginEvent.Deserializer())
        } else
            login_button.isEnabled = true
    }

    private fun register() {
        findNavController().navigate(R.id.action_loginFragment_to_groupChoiceRegistrationFragment, null)
    }

    private fun validate(): Boolean {
        var isValid = true
        val username = login_username.text
        val password = login_password.text

        if (username == null || username.isEmpty()) {
            login_username.error = getString(R.string.field_required)
            isValid = false
        }
        if (password == null || password.isEmpty()) {
            login_password.error = getString(R.string.field_required)
            isValid = false
        }
        return isValid
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLoginEvent(event: LoginEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        login_button.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
            val attended_groups = event.attended_groups
            prefs.attended_groups = attended_groups
            if (prefs.active_group_id == 0)
                prefs.active_group_id = attended_groups.first()
            Toast.makeText(context, getString(R.string.login_successful_text), Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_loginFragment_to_mainPageFragment, null)
        }
        else
            Toast.makeText(context, getString(R.string.login_unsuccessful_text), Toast.LENGTH_SHORT)
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        login_button.isEnabled = true
        super.onServerErrorEvent(event)
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
