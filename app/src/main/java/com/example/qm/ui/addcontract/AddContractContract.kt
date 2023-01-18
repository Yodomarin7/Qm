package com.example.qm.ui.addcontract

import com.example.qm.helper.MyErr
import com.example.qm.source.room.ContactEntity
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState

class AddContractContract {
    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: Boolean
    ): BaseState

    sealed class Event: BaseEvent {
        class SetContact(val contact: ContactEntity, val currency: String): Event()
    }

    sealed class Nav: BaseNav {
        class ToBackStack(val saved: Boolean): Nav()
    }
}