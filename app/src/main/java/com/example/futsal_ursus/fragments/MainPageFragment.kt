package com.example.futsal_ursus.fragments

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.R
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_main_page.*

class MainPageFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_main_page
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        main_page_match_present.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainPageFragment_to_playersListFragment,
                bundleOf("group_id" to 1, "event_id" to 2)
            )
        }
        floating_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }
    }
}