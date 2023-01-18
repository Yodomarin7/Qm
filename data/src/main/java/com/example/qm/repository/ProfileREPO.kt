package com.example.qm.repository

import com.example.qm.model.Profile
import com.example.qm.source.firebase.MyRemoteProfile
import com.example.qm.source.sharpref.MyLocalProfile

class ProfileREPO(
    private val myLocalData: MyLocalProfile,
    private val myRemoteData: MyRemoteProfile
) {

    suspend fun getProfile(): Profile? {
        return myLocalData.getMyProfile() ?: myRemoteData.getProfile()
    }

    suspend fun setProfile(profile: Profile): Boolean {
        return if(myRemoteData.setProfile(profile)) {
            myLocalData.saveMyProfile(profile)
            true
        } else { false }
    }

    fun deleteLocalProfile() { myLocalData.deleteMyProfile() }
}