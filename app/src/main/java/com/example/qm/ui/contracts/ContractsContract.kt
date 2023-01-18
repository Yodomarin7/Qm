package com.example.qm.ui.contracts

import com.example.qm.helper.MyErr
import com.example.qm.source.room.ContractEntity
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState

class ContractsContract {
    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: List<ContractEntity>?,
    ): BaseState

    sealed class Event: BaseEvent {
        class GetContracts(val currency: String): Event()
    }

    sealed class Nav: BaseNav {
        object ToBackStack : Nav()
    }
}