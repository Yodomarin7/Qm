package com.example.qm.repository

import com.example.qm.source.firebase.Contract
import com.example.qm.source.room.ContractDao
import com.example.qm.source.room.ContractEntity
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow

class ContractREPO(
    private val remoteContract: Contract,
    private val contractDao: ContractDao
) {

    suspend fun setLocalContract(contract: ContractEntity) {
        contractDao.insertOne(contract)
    }

    suspend fun getContractsByCurrency(currency: String): List<ContractEntity> {
        return contractDao.getAllByCurrency(currency)
    }

    suspend fun deleteLocalContracts(query: String) {
        contractDao.deleteByQuery(query)
    }

    fun getUser1Query(): Flow<QuerySnapshot> {
        return remoteContract.getUser1Query()
    }

    fun getUser2Query(): Flow<QuerySnapshot> {
        return remoteContract.getUser2Query()
    }

    suspend fun setRemoteContract(currency: String): String? {
        return remoteContract.setContract(currency)
    }

}

















