package co.techinsports.futsal_ursus.fragments

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.IntentFilter
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.fragment.findNavController
import co.techinsports.futsal_ursus.AppSettings.Companion.getUrl
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.activities.*
import co.techinsports.futsal_ursus.models.data.Credentials
import co.techinsports.futsal_ursus.models.events.LoginEvent
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

var devices = ArrayList<BluetoothDevice>()
var devicesMap = HashMap<String, BluetoothDevice>()
var mArrayAdapter: ArrayAdapter<String>? = null
val uuid: UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")

class LoginFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_login
    override fun eventBusEnabled(): Boolean = true

    inner class CustomBackPressedCallback : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAffinity((activity as MainActivity))
        }
    }

    fun lookForBluetoothDevices(){
//        this.textView = findViewById(R.id.textView)
        login_button.setOnClickListener {
            if (BluetoothAdapter.getDefaultAdapter() == null) {
                Snackbar.make(it, "Bluetooth is disabled", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()

            } else {
                devicesMap = HashMap()
                devices = ArrayList()
                mArrayAdapter!!.clear()

//                val editText = findViewById<EditText>(R.id.editText)
//                message = editText.text.toString()
//                editText.text.clear()
                for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) {
                    devicesMap.put(device.address, device)
                    devices.add(device)
                    // Add the name and address to an array adapter to show in a ListView
                    mArrayAdapter!!.add((if (device.name != null) device.name else "Unknown") + "\n" + device.address + "\nPared")
                }

                // Start discovery process
                if (BluetoothAdapter.getDefaultAdapter().startDiscovery()) {
//                    val dialog = SelectDeviceDialog()
//                    dialog.show(supportFragmentManager, "select_device")
                    println("started discovery")
                }
            }
        }
    }

    override fun initFragment(view: View) {
        val mainActivity = (activity as MainActivity)
        val callback = CustomBackPressedCallback()
        mainActivity.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        login_username.requestFocus()
//        login_button.setOnClickListener {
//            login()
//        }
//        register_button.setOnClickListener {
//            register()
//        }

        mArrayAdapter = ArrayAdapter(context, 0)
        lookForBluetoothDevices()
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
    override fun onLoginEvent(event: LoginEvent) {
        EventBus.getDefault().removeStickyEvent(event)
        login_button.isEnabled = true
        if (event.success) {
            prefs.login_token = event.token
            val attended_groups = event.attended_groups
            prefs.attended_groups = attended_groups
            //TODO: obsłużyć zmianę grupy - uwaga, jeśli zmieni się ID grupy to podczas logowania musi ono być jakoś zaktualizowane
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        login_button.isEnabled = true
        prefs.login_token = null
        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
