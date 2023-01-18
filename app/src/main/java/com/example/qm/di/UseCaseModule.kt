package com.example.qm.di

import com.example.qm.repository.ContactREPO
import com.example.qm.repository.ContractREPO
import com.example.qm.source.firebase.Contact
import com.example.qm.usecase.GetContracts
import com.example.qm.usecase.SetContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetContracts(
        contractREPO: ContractREPO,
        contactREPO: ContactREPO
    ): GetContracts { return GetContracts(contractREPO, contactREPO) }

    @Provides
    fun provideSetContract(
        contactREPO: ContactREPO,
        contractREPO: ContractREPO
    ): SetContract { return SetContract(contactREPO, contractREPO) }
}