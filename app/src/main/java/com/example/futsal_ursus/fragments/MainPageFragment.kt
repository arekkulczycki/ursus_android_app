package com.example.futsal_ursus.fragments

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Event
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.participant_chart.view.*
import kotlinx.android.synthetic.main.top_bar.*
import kotlin.math.max

class MainPageFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_main_page
    override fun eventBusEnabled(): Boolean = true

    private var next_training_participants_present: Int = 0
    private var next_training_participants_max: Int = 0
    private var next_match_participants_present: Int = 0
    private var next_match_participants_max: Int = 0

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            findNavController().navigate(R.id.action_logout)
        top_bar_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }

        initData()
//        syncData()
        initAnimations()

        main_page_match_present.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainPageFragment_to_playersListFragment,
                bundleOf("group_id" to 1, "event_id" to 2)
            )
        }
        main_page_training_present.setOnClickListener {
            runAnimation(main_page_training_present, main_page_training_absent)
        }
        main_page_training_absent.setOnClickListener {
            runAnimation(main_page_training_absent, main_page_training_present)
        }
        main_page_match_present.setOnClickListener {
            runAnimation(main_page_match_present, main_page_match_absent)
        }
        main_page_match_absent.setOnClickListener {
            runAnimation(main_page_match_absent, main_page_match_present)
        }
    }

    private fun syncData() {
        val url = AppSettings.getUrl("/events/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Event>
            prefs.next_training_datetime = it[0].datetime.toString()
            prefs.next_training_address = it[0].address
            prefs.present_next_training = it[0].present
            next_training_participants_present = it[0].participants_present
            next_training_participants_max = it[0].participants_max

            prefs.next_match_datetime = it[1].datetime.toString()
            prefs.next_match_address = it[1].address
            prefs.present_next_match = it[1].present
            next_match_participants_present = it[0].participants_present
            next_match_participants_max = it[0].participants_max

            initAnimations()
            initData()
        }, deserializer = Event.Deserializer())
    }

    private fun initData() {
        training_datetime.text = prefs.next_training_datetime
        training_location.text = prefs.next_training_address
        match_datetime.text = prefs.next_match_datetime
        match_location.text = prefs.next_match_address
    }

    private fun initAnimations() {
        if (prefs.present_next_training == true)
            runAnimation(main_page_training_present, main_page_training_absent)
        else if (prefs.present_next_training == false)
            runAnimation(main_page_training_absent, main_page_training_present)
        if (prefs.present_next_match == true)
            runAnimation(main_page_match_present, main_page_match_absent)
        else if (prefs.present_next_match == false)
            runAnimation(main_page_match_absent, main_page_match_present)

        //TODO: animacja wykresów obecności
        next_match_participants_present = 7
        next_match_participants_max = 12
        val coefficient = (next_match_participants_present / next_match_participants_max).toFloat()
        val max_height = chart_total.height
        println(max_height)
        println(chart_total.layoutParams.height)
        val height = (coefficient * max_height).toInt()
        println(height)
        val valueAnimator = ValueAnimator.ofInt(0, height)
        valueAnimator.addUpdateListener {
            chart_bottom.layoutParams.height = it.animatedValue as Int
        }
        valueAnimator.duration = 500
        valueAnimator.interpolator = AccelerateInterpolator(0.5f)
        valueAnimator.start()
    }

    private fun runAnimation(clicked_button: ImageButton, other_button: ImageButton) {
        val rotation = AnimationUtils.loadAnimation(context, R.anim.single_rotation_right)
        clicked_button.startAnimation(rotation)
        clicked_button.alpha = 1f

        val valueAnimator = ValueAnimator.ofFloat(1f, 0.2f)
        valueAnimator.addUpdateListener { other_button.alpha = it.animatedValue as Float }
        valueAnimator.duration = 200
        valueAnimator.interpolator = AccelerateInterpolator(0.5f)
        valueAnimator.start()
    }
}