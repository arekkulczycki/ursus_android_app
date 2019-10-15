package com.example.futsal_ursus.fragments

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Space
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.futsal_ursus.AppSettings
import com.example.futsal_ursus.R
import com.example.futsal_ursus.models.data.Event
import com.example.futsal_ursus.models.events.UnauthorizedEvent
import com.example.futsal_ursus.network.APIRequest
import com.example.futsal_ursus.prefs
import kotlinx.android.synthetic.main.fragment_main_page.*
import kotlinx.android.synthetic.main.participant_chart.view.*
import kotlinx.android.synthetic.main.top_bar.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainPageFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_main_page
    override fun eventBusEnabled(): Boolean = true

    private var next_training: Event? = null  //TODO: zmienić na lateinit i ładować dopóki nie znajdzie, a jak nie znajdzie to widok offline
    private var next_match: Event? = null

    override fun initFragment(view: View) {
        if (prefs.login_token.isNullOrEmpty())
            findNavController().navigate(R.id.action_logout)
        top_bar_settings_button.setOnClickListener {
            findNavController().navigate(R.id.action_global_settingsFragment)
        }

        initData()
        if (prefs.active_group_id > 0)
            syncData()

        training_chart_container.setOnClickListener {
            if (next_training != null)
                findNavController().navigate(
                    R.id.action_mainPageFragment_to_playersListFragment,
                    bundleOf("event_id" to next_training?.id)
                )
        }
        match_chart_container.setOnClickListener {
            if (next_match != null)
                findNavController().navigate(
                    R.id.action_mainPageFragment_to_playersListFragment,
                    bundleOf("event_id" to next_match?.id)
                )
        }
        //TODO: uporządkować poniższe
        main_page_training_present.setOnClickListener {
            if (prefs.present_next_training != true){
                runAnimation(main_page_training_present, main_page_training_absent)
                postInfo(next_training, true, "training_present", { prefs.present_next_training = true })
            }
        }
        main_page_training_absent.setOnClickListener {
            if (prefs.present_next_training != false){
                runAnimation(main_page_training_absent, main_page_training_present)
                postInfo(next_training, false, "training_absent", { prefs.present_next_training = false })
            }
        }
        main_page_match_present.setOnClickListener {
            if (prefs.present_next_match != true) {
                runAnimation(main_page_match_present, main_page_match_absent)
                postInfo(next_match, true, "match_present", { prefs.present_next_match = true })
            }
        }
        main_page_match_absent.setOnClickListener {
            if (prefs.present_next_match != false) {
                runAnimation(main_page_match_absent, main_page_match_present)
                postInfo(next_match, false, "match_absent", { prefs.present_next_match = false })
            }
        }
    }

    private fun postInfo(event: Event?, present: Boolean, flag: String, func: () -> Unit) {
        if (event == null)
            return
        val body = mapOf("present" to present, "event_id" to event.id)
        val url = AppSettings.getUrl("/present/")
        APIRequest().post(url, body, {
            func()
            val toAddStart = if (event.present == true && present) -1 else if (event.present == false && !present) 1 else 0
            val toAdd = if (event.present == true) if (present) 0 else -1 else if (present) 1 else 0
            val start_coefficient = (event.participants_present + toAddStart).toFloat() / event.participants_max.toFloat()
//            event.participants_present += toAdd
            val coefficient = (event.participants_present + toAdd).toFloat() / event.participants_max.toFloat()
            if ("training" in flag){
                runChartAnimation(training_chart_container.chart, training_chart_container.space, coefficient, start_coefficient)
                training_chart_container.chart_text.text = (event.participants_present + toAdd).toString()
            }
            if ("match" in flag){
                runChartAnimation(match_chart_container.chart, match_chart_container.space, coefficient, start_coefficient)
                match_chart_container.chart_text.text = (event.participants_present + toAdd).toString()
            }
        }, flag = flag)
    }

    private fun syncData() {
        val url = AppSettings.getUrl("/events/${prefs.active_group_id}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST")
            it as List<Event>
            if (it.count() == 2) {
                // dane pierwszego wydarzenia
                next_training = it[0]
                prefs.next_training_datetime = next_training?.start_date.toString()
                prefs.next_training_address = next_training?.address
                prefs.present_next_training = next_training?.present

                // dane drugiego wydarzenia
                next_match = it[1]
                prefs.next_match_datetime = next_match?.start_date.toString()
                prefs.next_match_address = next_match?.address
                prefs.present_next_match = next_match?.present

                initAnimations()
                initData()
            }
        }, deserializer = Event.Deserializer())
    }

    private fun initData() {
        training_datetime.text = prefs.next_training_datetime
        training_location.text = prefs.next_training_address
        match_datetime.text = prefs.next_match_datetime
        match_location.text = prefs.next_match_address
        training_chart_container.chart_text.text = next_training?.participants_present.toString()
        match_chart_container.chart_text.text = next_match?.participants_present.toString()
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

        if (next_training != null) {
            val training_coefficient = next_training!!.participants_present.toFloat() / next_training!!.participants_max.toFloat()
            runChartAnimation(training_chart_container.chart, training_chart_container.space, training_coefficient)
        }
        if (next_match != null) {
            val match_coefficient = next_match!!.participants_present.toFloat() / next_match!!.participants_max.toFloat()
            runChartAnimation(match_chart_container.chart, match_chart_container.space, match_coefficient)
        }
    }

    private fun runChartAnimation(chart: View, space: Space, coefficient: Float, start_coefficient: Float = 0f) {
        val chart_params = (chart.layoutParams as LinearLayout.LayoutParams)
        val space_params = (space.layoutParams as LinearLayout.LayoutParams)
        val valueAnimator = ValueAnimator.ofFloat(start_coefficient, coefficient)
        valueAnimator.addUpdateListener {
            chart_params.weight = it.animatedValue as Float
            space_params.weight = 1f - it.animatedValue as Float
            try {
                chart.requestLayout()
                space.requestLayout()
            } catch(e: Exception) {}
        }
        valueAnimator.duration = 3000
        valueAnimator.interpolator = DecelerateInterpolator(3f)
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        //TODO: action_logout
        Toast.makeText(context, getString(R.string.token_expired), Toast.LENGTH_SHORT)
            .show()
    }
}