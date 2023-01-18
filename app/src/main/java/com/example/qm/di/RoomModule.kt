package com.example.qm.di

import android.content.Context
import com.example.qm.AppDatabase
import com.example.qm.source.room.ContactDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import androidx.room.Room
import com.example.qm.source.room.ContractDao

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideContactDao(db: AppDatabase): ContactDao { return db.contactDao() }

    @Provides
    fun provideContractDao(db: AppDatabase): ContractDao { return db.contractDao() }

    @Provides
    @Singleton
    fun provideDB(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "myapp_database"
        ).build()
    }

}