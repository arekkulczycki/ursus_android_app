package co.techinsports.futsal_ursus.fragments

import android.view.View
import co.techinsports.futsal_ursus.R

class UserRegistrationFragment : BaseFragment() {
    override val layoutResource: Int = R.layout.fragment_user_registration
    override fun eventBusEnabled(): Boolean = true

    override fun initFragment(view: View) {

    }
}