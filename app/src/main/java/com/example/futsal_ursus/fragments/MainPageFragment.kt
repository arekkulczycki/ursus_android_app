package com.example.futsal_ursus.fragments

import android.view.View
import com.example.futsal_ursus.R
import com.example.futsal_ursus.prefs

class MainPageFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_main_page
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {

    }
}