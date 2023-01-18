package com.example.qm.source.sharpref

import android.content.Context
import com.example.qm.model.Profile

private const val SHARED_PREFS_NAME = "MyProfile"

private const val MY_FIRST_NAME = "my_first_name"
private const val MY_LAST_NAME = "my_last_name"

class MyLocalProfile (context: Context) {
    private val sharedPreferences =
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    fun getMyProfile(): Profile? {
        var result: Profile? = null

        val firstName = sharedPreferences.getString(MY_FIRST_NAME, null)
        val lastName = sharedPreferences.getString(MY_LAST_NAME, null)

        if (firstName != null && lastName != null) {
            result = Profile(firstName, lastName)
        } else { deleteMyProfile() }

        return result
    }

    fun saveMyProfile(profile: Profile) {
        sharedPreferences.edit().putString(MY_FIRST_NAME, profile.firstname).apply()
        sharedPreferences.edit().putString(MY_LAST_NAME, profile.lastName).apply()
    }

    fun deleteMyProfile() {
        sharedPreferences.edit().remove(MY_FIRST_NAME).apply()
        sharedPreferences.edit().remove(MY_LAST_NAME).apply()
    }

}