package com.example.qm

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.qm.source.room.ContactDao
import com.example.qm.source.room.ContactEntity
import com.example.qm.source.room.ContractDao
import com.example.qm.source.room.ContractEntity

@Database(
    version = 1,
    entities = [
        ContactEntity::class,
        ContractEntity::class,
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun contractDao(): ContractDao
}