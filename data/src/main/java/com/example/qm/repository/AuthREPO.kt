package com.example.qm.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthREPO  {

    suspend fun isAuth(): Boolean  {
        val user = Firebase.auth.currentUser

        return if(user == null) {
            return false
        } else {
            suspendCoroutine { continuation ->
                val token = user.getIdToken(true)

                token.addOnFailureListener { e->
                    if(e is FirebaseAuthInvalidUserException) {
                        continuation.resume(false)
                    } else {
                        continuation.resumeWithException(e)
                    }
                }
                token.addOnCanceledListener {
                    continuation.resumeWithException(Exception("Task has been canceled"))
                }
                token.addOnSuccessListener {
                    continuation.resume(true)
                }
            }
        }
    }

    fun listenAuthStatus(): Flow<Boolean> = callbackFlow {

        val listener = FirebaseAuth.IdTokenListener {
            launch {
                try{
                    if(!isAuth()) { trySend(false) }
                } catch (_: Exception) {

                }
            }
        }
        FirebaseAuth.getInstance().addIdTokenListener(listener)

        awaitClose {
            Log.e("N", "awaitClose")
            FirebaseAuth.getInstance().removeIdTokenListener(listener)
        }
    }.buffer(Channel.CONFLATED)
}