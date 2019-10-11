package com.example.futsal_ursus.fragments

import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.R
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_settings
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        logout_button.setOnClickListener {
            logout()
        }
    }

    private fun logout(){
        prefs.login_token = null
        findNavController().navigate(R.id.action_logout)
    }
}