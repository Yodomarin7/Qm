package com.example.qm.ui.login

import com.example.qm.helper.MyErr
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState

class LoginContract {
    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: Boolean
    ): BaseState

    sealed class Event: BaseEvent {
        class SetError(val msg: String): Event()
        object ToBackStack: Event()
    }

    sealed class Nav: BaseNav {
        object LaunchLogin: Nav()

        object ToBackStack: Nav()
    }

}