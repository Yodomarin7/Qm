package com.example.qm.ui.addcontract

import androidx.lifecycle.viewModelScope
import com.example.qm.R
import com.example.qm.helper.MyErr
import com.example.qm.helper.expToMyErr
import com.example.qm.source.room.ContactEntity
import com.example.qm.ui.base.BaseViewModel
import com.example.qm.usecase.SetContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddContractViewModel @Inject constructor(
    private val setContract: SetContract
): BaseViewModel<AddContractContract.Event, AddContractContract.State, AddContractContract.Nav>() {
    override fun initState(): AddContractContract.State = AddContractContract.State(
        showProgress = null, fail = null, success = true
    )

    override fun setEvent(event: AddContractContract.Event) {
        when(event) {
            is AddContractContract.Event.SetContact -> { setContact(event.contact, event.currency) }
        }
    }

    private fun setContact(contact: ContactEntity, currency: String) {
        setState { copy(showProgress = R.string.load_data) }

        viewModelScope.launch {
            try {
                if(setContract.run(contact, currency)) {
                    setState { copy(showProgress = R.string.processing_data) }
                    setNav(AddContractContract.Nav.ToBackStack(true))
                } else {
                    setState { copy(showProgress = null,
                        fail = MyErr.Res(R.string.not_login, toast = true)) }
                }
            } catch (e: Exception) {
                setState { copy(showProgress = null, fail = expToMyErr(e, toast = true)) }
            }
        }

    }

}