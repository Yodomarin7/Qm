package com.example.qm.source.firebase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Contract {

    fun getUser2Query(): Flow<QuerySnapshot> {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        return db.collection("contracts").whereEqualTo("user2", user!!.uid).snapshots()
    }

    fun getUser1Query(): Flow<QuerySnapshot> {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        return db.collection("contracts").whereEqualTo("user1", user!!.uid).snapshots()
    }

    suspend fun setContract(currency: String): String? {
        return suspendCoroutine { continuation ->
            val user = Firebase.auth.currentUser
            val db = Firebase.firestore

            if(user != null) {
                val data = hashMapOf(
                    "user1" to user.uid,
                    "currency" to currency,
                    "createAt" to FieldValue.serverTimestamp())

                db.collection("contracts").add(data)
                    .addOnFailureListener { e->
                        continuation.resumeWithException(e)
                    }
                    .addOnSuccessListener {
                        continuation.resume(it.id)
                    }
            } else{
                continuation.resume(null)
            }
        }
    }
}