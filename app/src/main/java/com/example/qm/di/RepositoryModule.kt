package com.example.qm.di

import com.example.qm.repository.*
import com.example.qm.source.firebase.Contact
import com.example.qm.source.firebase.Contract
import com.example.qm.source.firebase.MyRemoteProfile
import com.example.qm.source.room.ContactDao
import com.example.qm.source.room.ContractDao
import com.example.qm.source.sharpref.MyLocalProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

    @Provides
    fun provideAuth(): AuthREPO { return AuthREPO() }

    @Provides
    fun provideContact(
        remoteContact: Contact,
        localContact: ContactDao): ContactREPO { return ContactREPO(remoteContact, localContact) }

    @Provides
    fun provideContract(
        remoteContract: Contract,
        contractDao: ContractDao
    ): ContractREPO { return ContractREPO(remoteContract, contractDao) }

    @Provides
    fun provideDuty(): DutiesREPO { return DutiesREPO() }

    @Provides
    fun provideProfile(
        myRemoteProfile: MyRemoteProfile,
        myLocalData: MyLocalProfile): ProfileREPO { return ProfileREPO(myLocalData, myRemoteProfile) }
}