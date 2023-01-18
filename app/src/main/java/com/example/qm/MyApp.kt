package com.example.qm

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {

//    var currantUser: FirebaseUser? = null

    override fun onCreate() {
        super.onCreate()

//        FirebaseAuth.AuthStateListener { auth ->
//            currantUser = auth.currentUser
//        }
    }
}