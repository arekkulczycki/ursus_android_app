package co.techinsports.futsal_ursus.fragments

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.os.bundleOf
import co.techinsports.futsal_ursus.AppSettings
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.databinding.FragmentMainPageBinding
import co.techinsports.futsal_ursus.models.data.Event
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import co.techinsports.futsal_ursus.network.APIRequest
import co.techinsports.futsal_ursus.prefs
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.text.SimpleDateFormat
import java.util.*


class MainPageFragment : BaseFragment<FragmentMainPageBinding>(FragmentMainPageBinding::inflate) {
    override fun eventBusEnabled(): Boolean = true

    private var nextTraining: Event? =
        null  //TODO: zmienić na lateinit i ładować dopóki nie znajdzie, a jak nie znajdzie to widok offline
    private var nextMatch: Event? = null

    private val binding get() = viewBinding as FragmentMainPageBinding

    override fun initFragment(view: View) {
        val navController = getNavController()
        if (prefs.login_token.isNullOrEmpty()) {
            navController.navigate(
                R.id.action_logout
            )
        }
        binding.mainPageTopBar.topBarSettingsButton.setOnClickListener {
            navController.navigate(R.id.action_global_settingsFragment)
        }

        initData()
        if (prefs.active_group_id > 0) syncData()

        binding.trainingChartContainer.chart.setOnClickListener {
            if (nextTraining != null) {
                navController.navigate(
                    R.id.action_mainPageFragment_to_playersListFragment, bundleOf(
                        "event_id" to nextTraining?.id,
                        "event_title" to "${nextTraining?.type_name}: ${nextTraining?.name}"
                    )
                )
            }
        }
        binding.matchChartContainer.chart.setOnClickListener {
            if (nextMatch != null) {
                navController.navigate(
                    R.id.action_mainPageFragment_to_playersListFragment, bundleOf(
                        "event_id" to nextMatch?.id,
                        "event_title" to "${nextMatch?.type_name}: ${nextMatch?.name}"
                    )
                )
            }
        }
        //TODO: czasami coś się wali z animacją jakby początkowa wartość była błędna po pierwszym ustawieniu flagi
        //TODO: uporządkować poniższe
        binding.mainPageTrainingPresent.setOnClickListener {
            if (prefs.present_next_training != true) {
                runAnimation(binding.mainPageTrainingPresent, binding.mainPageTrainingAbsent)
                postInfo(
                    nextTraining,
                    true,
                    "training_present",
                    { prefs.present_next_training = true })
            }
        }
        binding.mainPageTrainingAbsent.setOnClickListener {
            if (prefs.present_next_training != false) {
                runAnimation(binding.mainPageTrainingAbsent, binding.mainPageTrainingPresent)
                postInfo(
                    nextTraining,
                    false,
                    "training_absent",
                    { prefs.present_next_training = false })
            }
        }
        binding.mainPageMatchPresent.setOnClickListener {
            if (prefs.present_next_match != true) {
                runAnimation(binding.mainPageMatchPresent, binding.mainPageMatchAbsent)
                postInfo(nextMatch, true, "match_present", { prefs.present_next_match = true })
            }
        }
        binding.mainPageMatchAbsent.setOnClickListener {
            if (prefs.present_next_match != false) {
                runAnimation(binding.mainPageMatchAbsent, binding.mainPageMatchPresent)
                postInfo(nextMatch, false, "match_absent", { prefs.present_next_match = false })
            }
        }
    }

    private fun postInfo(event: Event?, present: Boolean, flag: String, func: () -> Unit) {
        if (event == null) return
        val body = mapOf("present" to present, "event_id" to event.id)
        val url = AppSettings.getUrl("/present/")
        APIRequest().post(url, body, {
            func()
            val toAddStart =
                if (event.present == true && present) -1 else if ((event.present == null || event.present == false) && !present) 1 else 0
            val toAdd = if (event.present == true) if (present) 0 else -1 else if (present) 1 else 0
            val start_coefficient =
                (event.participants_present + toAddStart).toFloat() / event.participants_max.toFloat()
//            event.participants_present += toAdd
            val coefficient =
                (event.participants_present + toAdd).toFloat() / event.participants_max.toFloat()
            if ("training" in flag) {
                runChartAnimation(
                    binding.trainingChartContainer.chart,
                    binding.trainingChartContainer.space,
                    coefficient,
                    start_coefficient
                )
                binding.trainingChartContainer.chartText.text =
                    (event.participants_present + toAdd).toString()
            }
            if ("match" in flag) {
                runChartAnimation(
                    binding.matchChartContainer.chart,
                    binding.matchChartContainer.space,
                    coefficient,
                    start_coefficient
                )
                binding.matchChartContainer.chartText.text =
                    (event.participants_present + toAdd).toString()
            }
        }, flag = flag)
    }

    private fun syncData() {
        val url = AppSettings.getUrl("/events/${prefs.active_group_id}/")
        APIRequest().get(url, {
            @Suppress("UNCHECKED_CAST") it as List<Event>
            if (it.count() == 2) {
                for (i in 1 until 50) if (binding.mainPageTrainingName != null && binding.mainPageMatchName != null) break
                Thread.sleep(10)
                apiReaction(it)
            }
        }, deserializer = Event.Deserializer())
    }

    private fun apiReaction(it: List<Event>) {
        @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("yyyy-MM-dd\nHH:mm")
        // dane pierwszego wydarzenia
        nextTraining = it[0]
        val training_date = nextTraining?.start_date
        prefs.next_training_datetime =
            if (training_date == null) "" else dateFormat.format(training_date)
        prefs.next_training_address = nextTraining?.address
        prefs.present_next_training = nextTraining?.present
        binding.mainPageTrainingName.text = nextTraining?.name

        // dane drugiego wydarzenia
        nextMatch = it[1]
        val match_date = nextMatch?.start_date
        prefs.next_match_datetime = if (match_date == null) "" else dateFormat.format(match_date)
        prefs.next_match_address = nextMatch?.address
        prefs.present_next_match = nextMatch?.present
        binding.mainPageMatchName.text = nextMatch?.name

        initAnimations()
        initData()
        checkTrainingTakePlace(
            nextTraining?.start_date,
            if (nextTraining != null) nextTraining!!.participants_present else 0
        )
        checkMatchCallUp(nextMatch?.called_up)
    }

    private fun initData() {
        binding.trainingAlertContainer.visibility = View.GONE
        binding.mainPageTrainingChoice.visibility = View.VISIBLE
        binding.trainingDatetime.text = prefs.next_training_datetime
        binding.trainingLocation.text = prefs.next_training_address
        binding.matchDatetime.text = prefs.next_match_datetime
        binding.matchLocation.text = prefs.next_match_address
        binding.trainingChartContainer.chartText.text =
            nextTraining?.participants_present.toString()
        binding.matchChartContainer.chartText.text = nextMatch?.participants_present.toString()
    }

    private fun checkTrainingTakePlace(training_date: Date?, participants: Int) {
        if (training_date == null) return
        val training_time = training_date.time
        val now_time = Date().time
        val diff = training_time - now_time
        val diff_hours = diff / (60 * 60 * 1000)
        if (diff_hours <= prefs.hours_before_event && participants < prefs.minimum_participants) {
            binding.trainingAlertContainer.visibility = View.VISIBLE
            binding.mainPageTrainingChoice.visibility = View.GONE
            @SuppressLint("SetTextI18n")
            binding.trainingAlertText.text =
                "${getString(R.string.training_cancelled)}\n" + "${getString(R.string.too_few_participants)} (min. ${prefs.minimum_participants})"
        } else {
            binding.trainingAlertContainer.visibility = View.GONE
            binding.mainPageTrainingChoice.visibility = View.VISIBLE
        }
    }

    private fun checkMatchCallUp(called_up: Boolean?) {
        if (called_up == true) {
            binding.matchAlertContainer.visibility = View.GONE
            binding.mainPageMatchChoice.visibility = View.VISIBLE
        } else {
            binding.matchAlertText.text = getString(R.string.not_called_up)
            binding.matchAlertContainer.visibility = View.VISIBLE
            binding.mainPageMatchChoice.visibility = View.GONE
        }
    }

    private fun initAnimations() {
        if (prefs.present_next_training == true) runAnimation(
            binding.mainPageTrainingPresent, binding.mainPageTrainingAbsent
        )
        else if (prefs.present_next_training == false) runAnimation(
            binding.mainPageTrainingAbsent, binding.mainPageTrainingPresent
        )
        if (prefs.present_next_match == true) runAnimation(
            binding.mainPageMatchPresent, binding.mainPageMatchAbsent
        )
        else if (prefs.present_next_match == false) runAnimation(
            binding.mainPageMatchAbsent, binding.mainPageMatchPresent
        )

        if (nextTraining != null) {
            val training_coefficient =
                nextTraining!!.participants_present.toFloat() / nextTraining!!.participants_max.toFloat()
            runChartAnimation(
                binding.trainingChartContainer.chart,
                binding.trainingChartContainer.space,
                training_coefficient
            )
        }
        if (nextMatch != null) {
            val match_coefficient =
                nextMatch!!.participants_present.toFloat() / nextMatch!!.participants_max.toFloat()
            runChartAnimation(
                binding.matchChartContainer.chart,
                binding.matchChartContainer.space,
                match_coefficient
            )
        }
    }

    private fun runChartAnimation(
        chart: View, space: View, coefficient: Float, start_coefficient: Float = 0f
    ) {
        val chart_params = (chart.layoutParams as LinearLayout.LayoutParams)
        val space_params = (space.layoutParams as LinearLayout.LayoutParams)
        val valueAnimator = ValueAnimator.ofFloat(start_coefficient, coefficient)
        valueAnimator.addUpdateListener {
            chart_params.weight = it.animatedValue as Float
            space_params.weight = 1f - it.animatedValue as Float
            try {
                chart.requestLayout()
                space.requestLayout()
            } catch (e: Exception) {
            }
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
    override fun onServerErrorEvent(event: ServerErrorEvent) {
        super.onServerErrorEvent(event)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onUnauthorizedEvent(event: UnauthorizedEvent) {
        getNavController().navigate(
            R.id.action_logout
        )
        Toast.makeText(context, getString(R.string.token_expired), Toast.LENGTH_SHORT).show()
        super.onUnauthorizedEvent(event)
    }

}