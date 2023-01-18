package com.example.qm.usecase

import android.util.Log
import com.example.qm.repository.ContactREPO
import com.example.qm.repository.ContractREPO
import com.example.qm.source.room.ContractEntity
import com.google.firebase.Timestamp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.math.BigDecimal

sealed class QueryActor {
    class User1(val map: MutableMap<String, BigDecimal>): QueryActor()
    class User2(val map: MutableMap<String, BigDecimal>): QueryActor()
}

class GetContracts(
    private val contractREPO: ContractREPO,
    private val contactREPO: ContactREPO
) {

    @OptIn(ObsoleteCoroutinesApi::class)
    suspend fun run(scope: CoroutineScope): Channel<MutableMap<String, BigDecimal>> {
        val resulQueryChannel = Channel<MutableMap<String, BigDecimal>>(Channel.CONFLATED)

        scope.launch(Dispatchers.Default) {

            Log.e("T", "A_SCOPE: ${Thread.currentThread().id}")
            val actor = actor<QueryActor> {
                var user1 = false; var user2 = false
                val user1Map: MutableMap<String, BigDecimal> = mutableMapOf()
                val user2Map: MutableMap<String, BigDecimal> = mutableMapOf()

                for(msg in channel) {
                    Log.e("T", "ACTOR: ${Thread.currentThread().id}")
                    val resultMap: MutableMap<String, BigDecimal> = mutableMapOf()
                    when(msg) {
                        is QueryActor.User1 -> { user1 = true
                            user1Map.clear(); user1Map.putAll(msg.map)
                            resultMap.putAll(user1Map)

                            user2Map.forEach { map->
                                val currencySum = resultMap.get(map.key)

                                if(currencySum == null) { resultMap.put(map.key, map.value) }
                                else { resultMap.put(map.key, currencySum + map.value) }
                            }
                        }
                        is QueryActor.User2 -> { user2 = true
                            user2Map.clear(); user2Map.putAll(msg.map)
                            resultMap.putAll(user2Map)

                            user1Map.forEach { map->
                                val currencySum = resultMap.get(map.key)

                                if(currencySum == null) { resultMap.put(map.key, map.value) }
                                else { resultMap.put(map.key, currencySum + map.value) }
                            }
                        }
                    }
                    if(user1 && user2) {
                        Log.e("D", "resulQueryChannel")
                        resulQueryChannel.send(resultMap)
                    }
                }
            }

            launch {
                Log.e("T", "U1_SCOPE: ${Thread.currentThread().id}")
                contractREPO.getUser1Query().collect { snapshot ->

                    Log.e("T", "U1: ${Thread.currentThread().id}")
                    actor.send(QueryActor.User1(getContract(snapshot.documents, "user1")))
                }
            }

            launch {
                Log.e("T", "U2_SCOPE: ${Thread.currentThread().id}")
                contractREPO.getUser2Query().collect { snapshot ->
                    Log.e("T", "U2: ${Thread.currentThread().id}")
                    actor.send(QueryActor.User2(getContract(snapshot.documents, "user2")))
                }
            }
        }

        return resulQueryChannel
    }

    private suspend fun getContract(documents: List<DocumentSnapshot>, query: String): MutableMap<String, BigDecimal> {
        val user = Firebase.auth.currentUser

        contractREPO.deleteLocalContracts(query)

        val currencyHM: MutableMap<String, BigDecimal> = mutableMapOf()

        documents.forEach { doc->
            val contractId = doc.id
            val contactId: String?

            val user1 = doc.data?.get("user1")
            val user2 = doc.data?.get("user2")
            val currency = doc.data?.get("currency")
            val sum = doc.data?.get("sum")
            val createAt =  doc.data?.get("createAt")

//                    if(user != null) Log.e("Contract", "1 user != null")
//                    if(user1 is String?) Log.e("Contract", "2 user1 is String?")
//                    if(user2 is String?) Log.e("Contract", "3 user2 is String?")
//                    if(currency is String?) Log.e("Contract", "4 currency is String?")
//                    if(sum is Double?) Log.e("Contract", "5 sum is Double?")
//                    if(createAt is Date?) Log.e("Contract", "6 createAt is Date?")
//                    if(( (user1 == user?.uid && user2 != user?.uid) || (user2 == user?.uid && user1 != user?.uid))) Log.e("Contract", "7 LONG USER")

            if( user != null &&
                user1 is String? &&
                user2 is String? &&
                currency is String? &&
                currency != null &&
                sum is Double? &&
                createAt is Timestamp? &&
                ( (user1 == user.uid && user2 != user.uid) || (user2 == user.uid && user1 != user.uid))
            ) {
                contactId = if(user1 != user.uid) user1 else user2
                val profile = contactREPO.getProfile(contractId, contactId)
                val _sum = sum ?: 0.0

                val contractEntity = ContractEntity(
                    contractId,
                    createAt?.toDate()?.time ?: 0,
                    profile?.firstname,
                    profile?.lastName,
                    contactId,
                    currency,
                    _sum,
                    query)

                val currencySum = currencyHM.get(currency)

                if(currencySum == null) { currencyHM.put(currency, _sum.toBigDecimal()) }
                else { currencyHM.put(currency, currencySum + _sum.toBigDecimal()) }

                contractREPO.setLocalContract(contractEntity)
                Log.e("Contract", "collect")
            } else {
                Log.e("Contract", "myContract.getContracts().collect { ..")
            }
        }

        return currencyHM
    }

//    val state = MutableStateFlow(mutableMapOf<String, BigDecimal>())
//    val state1 = MutableStateFlow(mutableMapOf<String, BigDecimal>())
//    val state2 = MutableStateFlow(mutableMapOf<String, BigDecimal>())
//    suspend fun run(scope: CoroutineScope): MutableStateFlow<MutableMap<String, BigDecimal>> {
//
//        val mutex = Mutex()
//
//            scope.launch(Dispatchers.IO) {
//                contractREPO.getUser1Query().collect { snapshot ->
//                    mutex.withLock {
//                        val mapAll = mutableMapOf<String, BigDecimal>()
//
//                        state1.value.clear()
//                        state1.value = getContract(snapshot.documents, "user1")
//                        mapAll.putAll(state1.value)
//
//                        state2.value.forEach { map->
//                            val currencySum = mapAll.get(map.key)
//
//                            if(currencySum == null) { mapAll.put(map.key, map.value) }
//                            else { mapAll.put(map.key, currencySum + map.value) }
//                        }
//
//                        state.value.clear()
//                        state.value = mapAll
//                    }
//                }
//            }
//
//            scope.launch(Dispatchers.IO) {
//                contractREPO.getUser2Query().collect { snapshot ->
//                    mutex.withLock {
//                        val mapAll = mutableMapOf<String, BigDecimal>()
//
//                        state2.value.clear()
//                        state2.value = getContract(snapshot.documents, "user2")
//                        mapAll.putAll(state2.value)
//
//                        state1.value.forEach { map->
//                            val currencySum = mapAll.get(map.key)
//
//                            if(currencySum == null) { mapAll.put(map.key, map.value) }
//                            else { mapAll.put(map.key, currencySum + map.value) }
//                        }
//
//                        state.value.clear()
//                        state.value = mapAll
//                    }
//                }
//            }
//
////            launch {
////                state.collect {
////                    Log.e("D", "STATE")
////                }
////            }
//        Log.e("D", "RETURN")
//        return state
//    }
}






















