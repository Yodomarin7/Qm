package com.example.qm.ui.currencies

import android.content.Context
import com.example.qm.helper.MyErr
import com.example.qm.model.SortCurrency
import com.example.qm.ui.base.BaseEvent
import com.example.qm.ui.base.BaseNav
import com.example.qm.ui.base.BaseState
import java.math.BigDecimal

class CurrenciesContract {

    data class State(
        val showProgress: Int?,
        val fail: MyErr?,
        val success: Map<String, BigDecimal>?,
        val sorting: SortCurrency
    ): BaseState

    sealed class Event: BaseEvent {
        object GetItems: Event()
        object SortByName: Event()
        object SortBySum: Event()
        class SetError(val msg: String): Event()
        class LogOut(val context: Context): Event()
    }

    sealed class Nav: BaseNav {
        object ToAddContact: Nav()
        object ToLogin: Nav()
        object ToProfile: Nav()
        class ToContracts(val currency: String): Nav()
    }

}