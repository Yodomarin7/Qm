package com.example.qm.source.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

const val CONTRACT_TABLE = "contract_table"

@Dao
interface ContractDao {
    @Query("SELECT * FROM $CONTRACT_TABLE")
    fun getAllAsFlow(): Flow<List<ContractEntity>>

    @Query("SELECT * FROM $CONTRACT_TABLE WHERE currency = :currency")
    suspend fun getAllByCurrency(currency: String): List<ContractEntity>

    @Query("SELECT * FROM $CONTRACT_TABLE WHERE contractId = :contractId")
    suspend fun getByContractId(contractId: String): ContractEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(contract: ContractEntity)

    @Delete
    suspend fun deleteOne(contract: ContractEntity)

    @Query("DELETE FROM $CONTRACT_TABLE WHERE query = :query")
    suspend fun deleteByQuery(query: String)

    @Query("DELETE FROM $CONTRACT_TABLE")
    suspend fun deleteAll()
}