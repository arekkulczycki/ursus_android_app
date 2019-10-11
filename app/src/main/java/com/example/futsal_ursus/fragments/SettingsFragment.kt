package com.example.futsal_ursus.fragments

import android.view.View
import com.example.futsal_ursus.R

class SettingsFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_settings
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {

    }
}