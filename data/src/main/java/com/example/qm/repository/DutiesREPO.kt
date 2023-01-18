package com.example.qm.repository

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow

class DutiesREPO {

    fun getItems(): Flow<QuerySnapshot> {
        val user = Firebase.auth.currentUser
        val db = Firebase.firestore

        return db.collection("agreements").whereEqualTo("user_1", user!!.uid)
            .whereEqualTo("user_2", user.uid).snapshots()

//        db.collection("agreements").whereEqualTo("user_1", user!!.uid)
//            .whereEqualTo("user_2", user.uid).get()
//            .addOnSuccessListener { documents ->
//                val data = CurrenciesContract.Model(items = hashMapOf())
//                val currencyHM = hashMapOf< String, HashMap<String, List<String>> >()
//
//                for(doc in documents) {
//                    try{
//                        var friend = ""
//                        var currency = ""
//
//                        if(doc.contains("user_1") && doc.contains("user_2") && doc.contains("currency")
//                            && doc.contains("kreditor") && doc.contains("sum")
//                        ) {
//                            friend = if(doc.data["user_1"] != user.uid) {
//                                doc.data["user_1"] as String
//                            } else if(doc.data["user_2"] != user.uid) {
//                                doc.data["user_2"] as String
//                            } else { continue }
//
//                            currency = doc.data["currency"] as String
//
//                            if(!currencyHM.contains(currency)) { currencyHM.set(currency, hashMapOf()) }
//
//                            val userHM = currencyHM.get(currency)
//                            userHM?.set(friend, listOf())
//                        }
//
//                    } catch (e: Exception) {  }
//                }
//
//                setState { copy(showProgress = false, error = "", data = data) }
//            }
//            .addOnFailureListener {
//                setState { copy(showProgress = false, error = it.message ?: "Error") }
//            }
    }
}