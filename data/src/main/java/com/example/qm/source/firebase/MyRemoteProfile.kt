package com.example.qm.source.firebase

import com.example.qm.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class MyRemoteProfile {

    suspend fun getProfile(): Profile? {
        return suspendCoroutine { continuation ->
            val user = Firebase.auth.currentUser
            val db = Firebase.firestore

            if (user != null) {
                db.collection("users").document(user.uid).get()
                    .addOnSuccessListener { doc ->
                        if (doc?.data?.get("firstName") is String &&
                            doc.data?.get("lastName") is String
                        ) {
                            continuation.resume(
                                Profile(
                                    doc.data!!["firstName"] as String,
                                    doc.data!!["lastName"] as String
                                )
                            )
                        } else {
                            continuation.resume(null)
                        }
                    }
                    .addOnFailureListener {
                        continuation.resumeWithException(it)
                    }
            } else {
                continuation.resume(null)
            }
        }
    }

    suspend fun setProfile(profile: Profile): Boolean {
        return suspendCoroutine { continuation ->
            val user = Firebase.auth.currentUser
            val db = Firebase.firestore

            if(user != null) {
                val data = hashMapOf(
                    "firstName" to profile.firstname,
                    "lastName" to profile.lastName)

                db.collection("users").document(user.uid)
                    .set(data, SetOptions.merge())
                    .addOnFailureListener { e->
                        continuation.resumeWithException(e)
                    }
                    .addOnSuccessListener {
                        continuation.resume(true)
                    }
            } else{
                continuation.resume(false)
            }
        }
    }
}