package com.example.qm.source.room

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.NOCASE
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = CONTRACT_TABLE
)
data class ContractEntity(
    @PrimaryKey val contractId: String,
    @ColumnInfo(name="createAt") val createAt: Long,
    @ColumnInfo(name="first_name") val firstName: String?,
    @ColumnInfo(name="last_name") val lastName: String?,
    @ColumnInfo(name="contactId") val contactId: String?,
    @ColumnInfo(name="currency", collate = NOCASE) val currency: String,
    @ColumnInfo(name="sum") val sum: Double,
    @ColumnInfo(name="query") val query: String,
)