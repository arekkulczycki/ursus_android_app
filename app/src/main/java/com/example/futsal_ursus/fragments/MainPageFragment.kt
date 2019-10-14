package com.example.futsal_ursus.fragments

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.R
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_main_page.*

class MainPageFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_main_page
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            findNavController().navigate(R.id.action_logout)
        main_page_match_present.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainPageFragment_to_playersListFragment,
                bundleOf("group_id" to 1, "event_id" to 2)
            )
        }
        floating_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }
        main_page_training_present.setOnClickListener {
            val rotation = AnimationUtils.loadAnimation(context, R.anim.single_rotation_right)
            main_page_training_present.startAnimation(rotation)

            val valueAnimator = ValueAnimator.ofFloat(1f, 0.2f)
            valueAnimator.addUpdateListener { main_page_training_absent.alpha = it.animatedValue as Float }
            valueAnimator.duration = 200
            valueAnimator.interpolator = AccelerateInterpolator(0.5f)
            valueAnimator.start()
        }
    }
}