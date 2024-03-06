package co.techinsports.futsal_ursus.fragments

import android.view.View
import android.widget.Toast
import co.techinsports.futsal_ursus.AppSettings
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.databinding.FragmentGroupChoiceRegistrationBinding
import co.techinsports.futsal_ursus.models.data.Credentials
import co.techinsports.futsal_ursus.models.data.Group
import co.techinsports.futsal_ursus.models.events.RegistrationEvent
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class GroupChoiceRegistrationFragment :
    BaseFragment<FragmentGroupChoiceRegistrationBinding>(FragmentGroupChoiceRegistrationBinding::inflate) {
    override fun eventBusEnabled(): Boolean = true

    var chosenGroupId: Int? = null

    private val binding get() = viewBinding as FragmentGroupChoiceRegistrationBinding

    override fun initFragment(view: View) {
        syncGroups()

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        binding.registerButton.isEnabled = false
        if (validate()) {
            val username = binding.registerUsername.text.toString()
            val password = binding.registerPassword.text.toString()
            val credentials = Credentials(username, password, chosenGroupId)
            val url: String = AppSettings.getUrl("/user/register/")
            APIRequest().post(url, credentials, {
                @Suppress("UNCHECKED_CAST")
                it as RegistrationEvent
                EventBus.getDefault().post(it)
            }, deserializer = RegistrationEvent.Deserializer())
        } else
            binding.registerButton.isEnabled = true
    }

    private fun validate(): Boolean {
        var isValid = true
        if (binding.registerPassword.text == null || binding.registerPassword.text.isEmpty()) {
            binding.registerPassword.error = getString(R.string.field_required)
            isValid = false
        }
        if (binding.registerConfirmPassword.text == null || binding.registerConfirmPassword.text.isEmpty()) {
            binding.registerConfirmPassword.error = getString(R.string.field_required)
            isValid = false
        }
        if (binding.registerPassword.text.toString() != binding.registerConfirmPassword.text.toString()) {
            binding.registerPassword.error = getString(R.string.field_required)
            binding.registerConfirmPassword.error = getString(R.string.field_required)
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
                chosenGroupId = it.first().id
        }, deserializer = Group.Deserializer())
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onRegisterEvent(event: RegistrationEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        binding.registerButton.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
            val attended_groups = event.attended_groups
            prefs.attended_groups = attended_groups
            prefs.active_group_id = attended_groups.first()
            Toast.makeText(context, getString(R.string.login_successful_text), Toast.LENGTH_SHORT)
                .show()
            getNavController().navigate(
                R.id.action_groupChoiceRegistrationFragment_to_mainPageFragment,
                null
            )
        } else if (event.reason == 0)
            Toast.makeText(context, getString(R.string.email_does_not_exist), Toast.LENGTH_SHORT)
                .show()
        else if (event.reason == 1)
            Toast.makeText(context, getString(R.string.group_does_not_exist), Toast.LENGTH_SHORT)
                .show()
        else if (event.reason == 2)
            Toast.makeText(
                context,
                getString(R.string.email_already_in_database),
                Toast.LENGTH_SHORT
            )
                .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        binding.registerButton.isEnabled = true
        super.onServerErrorEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        binding.registerButton.isEnabled = true
        prefs.login_token = null
        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = GroupChoiceRegistrationFragment()
    }
}
