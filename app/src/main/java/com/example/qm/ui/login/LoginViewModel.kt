package com.example.qm.ui.login

import androidx.lifecycle.viewModelScope
import com.example.qm.R
import com.example.qm.helper.expToMyErr
import com.example.qm.helper.strToMyErr
import com.example.qm.repository.AuthREPO
import com.example.qm.repository.ProfileREPO
import com.example.qm.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authREPO: AuthREPO,
    private val profileREPO: ProfileREPO
): BaseViewModel<LoginContract.Event, LoginContract.State, LoginContract.Nav>() {

    override fun initState(): LoginContract.State = LoginContract.State(showProgress = R.string.load_data,
        fail = null, success = false)

    init {
        viewModelScope.launch {
            try{
                if(authREPO.isAuth()) {
                    setState { copy(showProgress = null, fail = null, success = true) }
                } else {
                    profileREPO.deleteLocalProfile()
                    setNav(LoginContract.Nav.LaunchLogin)
                }
            } catch (e: Exception) {
                setState { copy(showProgress = null, fail = expToMyErr(e), success = false) }
            }
        }
    }

    override fun setEvent(event: LoginContract.Event) {
        when(event) {
            is LoginContract.Event.SetError -> {
                setState { copy(showProgress = null, fail = strToMyErr(event.msg), success = false) }
            }
            LoginContract.Event.ToBackStack -> { setNav(LoginContract.Nav.ToBackStack) }
        }
    }
}














