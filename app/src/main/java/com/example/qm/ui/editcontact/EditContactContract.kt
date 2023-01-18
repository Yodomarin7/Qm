package com.example.qm.ui.editcontact

import com.example.qm.helper.MyErr
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState

class EditContactContract {
    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: Boolean
    ): BaseState

    sealed class Event: BaseEvent {

    }

    sealed class Nav: BaseNav {
        object ToBackStack : Nav()
    }
}