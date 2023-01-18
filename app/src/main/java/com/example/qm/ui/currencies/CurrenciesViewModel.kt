package com.example.qm.ui.currencies

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.qm.R
import com.example.qm.helper.strToMyErr
import com.example.qm.model.SortCurrency
import com.example.qm.repository.AuthREPO
import com.example.qm.repository.ProfileREPO
import com.example.qm.source.sharpref.Currencies
import com.example.qm.ui.base.BaseViewModel
import com.example.qm.usecase.GetContracts
import com.firebase.ui.auth.AuthUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CurrenciesViewModel @Inject constructor(
    private val authREPO: AuthREPO,
    private val profileREPO: ProfileREPO,
    private val getContracts: GetContracts,
    private val currencies: Currencies
) : BaseViewModel<CurrenciesContract.Event, CurrenciesContract.State, CurrenciesContract.Nav>() {

    override fun initState() = CurrenciesContract.State(
        showProgress = R.string.load_data,
        fail = null,
        success = null,
        sorting = SortCurrency.ByName
    )

    init {
        setState { copy(sorting = currencies.getSorting()) }

        viewModelScope.launch {
            authREPO.listenAuthStatus().collect {
                setNav(CurrenciesContract.Nav.ToLogin)
            }
        }
        getItems()
    }

    override fun setEvent(event: CurrenciesContract.Event) {
        when (event) {
            is CurrenciesContract.Event.GetItems -> {
                getItems()
            }
            is CurrenciesContract.Event.LogOut -> {
                logOut(event.context)
            }
            is CurrenciesContract.Event.SetError -> {
                setState {
                    copy(
                        showProgress = null,
                        fail = strToMyErr(event.msg)
                    )
                }
            }
            CurrenciesContract.Event.SortByName -> {
                currencies.setSorting(SortCurrency.ByName)
                sortSuccess(SortCurrency.ByName, state.value.success)
            }
            CurrenciesContract.Event.SortBySum -> {
                currencies.setSorting(SortCurrency.BySum)
                sortSuccess(SortCurrency.BySum, state.value.success)
            }
        }
    }

    private fun sortSuccess(sort: SortCurrency, map: Map<String, BigDecimal>?) {
        if(map != null) {
            when(sort) {
                SortCurrency.ByName -> { setState {
                    copy(showProgress = null, fail = null, success = map.toSortedMap().toMap(), sorting = sort) }
                }
                SortCurrency.BySum -> {
                    val newMap = map.toList().sortedBy { (_, value) -> value }.toMap()
                    setState { copy(showProgress = null, fail = null, success = newMap, sorting = sort) }
                }
            }
        }
    }

    private var jobItems: Job? = null

    private fun getItems() {
        setState { copy(showProgress = R.string.load_data) }
        jobItems?.cancel()

        val exception = CoroutineExceptionHandler { _, e ->
            setState { copy(showProgress = null, fail = strToMyErr(e.message, singly = true)) }
        }

        jobItems = viewModelScope.launch(exception) {
            Log.e("T", "VM: ${Thread.currentThread().id}")

            if (!authREPO.isAuth()) {
                setNav(CurrenciesContract.Nav.ToLogin)
            } else if (profileREPO.getProfile() == null) {
                setNav(CurrenciesContract.Nav.ToProfile)
            } else {
                getContracts.run(this).receiveAsFlow().collect {
                    sortSuccess(state.value.sorting, it)
                }
            }
        }
    }

    private fun logOut(context: Context) {
        AuthUI.getInstance().signOut(context)
    }

}