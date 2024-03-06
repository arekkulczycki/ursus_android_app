package co.techinsports.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import co.techinsports.futsal_ursus.AppSettings.Companion.getUrl
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.activities.MainActivity
import co.techinsports.futsal_ursus.databinding.FragmentLoginBinding
import co.techinsports.futsal_ursus.models.data.Credentials
import co.techinsports.futsal_ursus.models.events.LoginEvent
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {
    override fun eventBusEnabled(): Boolean = true

    private val binding get() = viewBinding as FragmentLoginBinding

    inner class CustomBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAffinity((activity as MainActivity))
        }
    }

    override fun initFragment(view: View) {
        val mainActivity = (activity as MainActivity)
        val callback = CustomBackPressedCallback()
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        binding.loginUsername.requestFocus()
        binding.loginButton.setOnClickListener {
            login()
        }
        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun login() {
        binding.loginButton.isEnabled = false
        if (validate()) {
            val username = binding.loginUsername.text.toString()
            val password = binding.loginPassword.text.toString()
            val credentials = Credentials(username, password)
            val url: String = getUrl("/user/token/")
            APIRequest().post(url, credentials, {
                @Suppress("UNCHECKED_CAST")
                it as LoginEvent
                EventBus.getDefault().post(it)
            }, deserializer = LoginEvent.Deserializer())
        } else
            binding.loginButton.isEnabled = true
    }

    private fun register() {
        getNavController().navigate(R.id.action_loginFragment_to_groupChoiceRegistrationFragment, null)
    }

    private fun validate(): Boolean {
        var isValid = true
        val username = binding.loginUsername.text
        val password = binding.loginPassword.text

        if (username == null || username.isEmpty()) {
            binding.loginUsername.error = getString(R.string.field_required)
            isValid = false
        }
        if (password == null || password.isEmpty()) {
            binding.loginPassword.error = getString(R.string.field_required)
            isValid = false
        }
        return isValid
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onLoginEvent(event: LoginEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        binding.loginButton.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
            val attended_groups = event.attended_groups
            prefs.attended_groups = attended_groups
            //TODO: obsłużyć zmianę grupy - uwaga, jeśli zmieni się ID grupy to podczas logowania musi ono być jakoś zaktualizowane
            prefs.active_group_id = attended_groups.first()
            Toast.makeText(context, getString(R.string.login_successful_text), Toast.LENGTH_SHORT)
                .show()
            getNavController().navigate(R.id.action_loginFragment_to_mainPageFragment, null)
        }
        else
            Toast.makeText(context, getString(R.string.login_unsuccessful_text), Toast.LENGTH_SHORT)
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        binding.loginButton.isEnabled = true
        super.onServerErrorEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        binding.loginButton.isEnabled = true
        prefs.login_token = null
        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
