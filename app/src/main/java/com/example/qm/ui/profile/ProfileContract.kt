package com.example.qm.ui.profile

import com.example.qm.helper.MyErr
import com.example.qm.model.Profile
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState

class ProfileContract {

    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: Profile?,
    ): BaseState

    sealed class Event: BaseEvent {
        object GetMyProfile: Event()
        class SetMyProfile(val profile: Profile): Event()
    }

    sealed class Nav: BaseNav {
        class ToBackStack(val saved: Boolean): Nav()
    }
}