package com.example.qm.source.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_table")
    fun getAll(): Flow<List<ContactEntity>>

    @Query("SELECT * FROM contact_table WHERE contract_id = :contractId")
    suspend fun getByContractId(contractId: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOne(contact: ContactEntity)

    @Delete
    suspend fun deleteOne(contact: ContactEntity)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()

}