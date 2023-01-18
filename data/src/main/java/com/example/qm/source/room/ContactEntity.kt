package com.example.qm.source.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "contact_table",
    indices = [
        Index("id", unique = true)
    ]
)
data class ContactEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "contract_id") val contractId: String,
    @ColumnInfo(name="first_name") val firstName: String?,
    @ColumnInfo(name="last_name") val lastName: String?,
)