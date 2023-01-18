package com.example.qm.ui.contracts

import androidx.lifecycle.viewModelScope
import com.example.qm.R
import com.example.qm.helper.strToMyErr
import com.example.qm.repository.ContractREPO
import com.example.qm.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContractsViewModel @Inject constructor(
    private val contractREPO: ContractREPO
): BaseViewModel<ContractsContract.Event, ContractsContract.State, ContractsContract.Nav>() {
    override fun initState(): ContractsContract.State = ContractsContract.State(
        showProgress = R.string.load_data,
        fail = null,
        success = null,
    )

    override fun setEvent(event: ContractsContract.Event) {
        when(event) {
            is ContractsContract.Event.GetContracts -> { getContacts(event.currency) }
        }
    }

    private fun getContacts(currency: String) {
        val exception = CoroutineExceptionHandler { _, e ->
            setState { copy(showProgress = null, fail = strToMyErr(e.message, singly = true)) }
        }

        viewModelScope.launch(exception) {
            val list = contractREPO.getContractsByCurrency(currency)
            setState { copy(showProgress = null, fail = null, success = list) }
        }
    }

}