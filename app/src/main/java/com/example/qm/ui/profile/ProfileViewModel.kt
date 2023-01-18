package com.example.qm.ui.profile

import androidx.lifecycle.viewModelScope
import com.example.qm.R
import com.example.qm.helper.MyErr
import com.example.qm.helper.expToMyErr
import com.example.qm.model.Profile
import com.example.qm.repository.ProfileREPO
import com.example.qm.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileREPO: ProfileREPO
) : BaseViewModel<ProfileContract.Event, ProfileContract.State, ProfileContract.Nav>() {
    override fun initState(): ProfileContract.State = ProfileContract.State(
        showProgress = R.string.load_data, fail = null, success = null
    )

    init {
        getProfile()
    }

    override fun setEvent(event: ProfileContract.Event) {
        when (event) {
            ProfileContract.Event.GetMyProfile -> {
                getProfile()
            }
            is ProfileContract.Event.SetMyProfile -> {
                setProfile(event.profile)
            }
        }
    }

    private fun setProfile(profile: Profile) {
        setState { copy(showProgress = R.string.save_data, fail = null, success = null) }

        viewModelScope.launch {
            try {
                if (profileREPO.setProfile(profile)) {
                    setState { copy(showProgress = R.string.processing_data) }
                    setNav(ProfileContract.Nav.ToBackStack(true))
                } else {
                    setState { copy(showProgress = null, fail = MyErr.Res(R.string.not_login, singly = true)) }
                }
            } catch (e: Exception) {
                setState { copy(showProgress = null, fail = expToMyErr(e, toast = true)) }
            }
        }
    }

    private fun getProfile() {
        setState { copy(showProgress = R.string.load_data, fail = null, success = null) }

        viewModelScope.launch {
            try {
                val profile = profileREPO.getProfile()
                if (profile != null) {
                    setState { copy(showProgress = null, fail = null, success = profile) }
                } else {
                    setState {
                        copy(
                            showProgress = null, fail = null, success = Profile(
                                firstname = "", lastName = ""
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                setState { copy(showProgress = null, fail = expToMyErr(e, singly = true)) }
            }
        }
    }

}