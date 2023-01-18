package com.example.qm.source.firebase

import com.example.qm.model.Profile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Contact {

    suspend fun getProfile(userId: String): Profile? {
        return suspendCoroutine { continuation ->
            val db = Firebase.firestore

            db.collection("users").document(userId).get()
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
        }
    }

}