package co.techinsports.futsal_ursus.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import co.techinsports.futsal_ursus.R
import co.techinsports.futsal_ursus.models.events.LoginEvent
import co.techinsports.futsal_ursus.models.events.ServerErrorEvent
import co.techinsports.futsal_ursus.models.events.UnauthorizedEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


abstract class BaseFragment : Fragment() {

    protected abstract val layoutResource: Int
    protected abstract fun initFragment(view: View)
    protected abstract fun eventBusEnabled(): Boolean

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view: View? = null
        val layoutRes = layoutResource
        if (layoutRes != -1) {
            view = inflater.inflate(layoutRes, container, false)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        restoreInstanceState(savedInstanceState)
        initFragment(view)
    }

    override fun onResume() {
        super.onResume()
        registerEventBus()
    }

    private fun registerEventBus() {
        val eventBus = EventBus.getDefault()
        if (eventBusEnabled() && !eventBus.isRegistered(this)) {
            eventBus.register(this)
        }
    }

    override fun onPause() {
        unregisterEventBus()
        super.onPause()
    }

    private fun unregisterEventBus() {
        val eventBus = EventBus.getDefault()
        if (eventBus.isRegistered(this)) {
            eventBus.unregister(this)
        }
    }

    //<editor-fold desc="Save state handling">

//    internal fun restoreInstanceState(savedInstanceState: Bundle?) {
//        unfreezeInstanceState(savedInstanceState)
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        // This is done in both onPause and onSaveInstanceState to prevent state-loss exceptions
//        unregisterEventBus()
//        super.onSaveInstanceState(outState)
//        freezeInstanceState(outState)
//    }

    protected fun showKeyboard(field: EditText) {
        val context = context
        if (context != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    protected fun hideKeyboard() {
        val context = context
        if (context != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = view
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onLoginEvent(event: LoginEvent) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onServerErrorEvent(event: ServerErrorEvent) {
        Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT)
            .show()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onUnauthorizedEvent(event: UnauthorizedEvent) {
    }

}
