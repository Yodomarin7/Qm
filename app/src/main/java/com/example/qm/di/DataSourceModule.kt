package com.example.qm.di

import android.content.Context
import com.example.qm.source.firebase.Contact
import com.example.qm.source.firebase.Contract
import com.example.qm.source.firebase.MyRemoteProfile
import com.example.qm.source.sharpref.Currencies
import com.example.qm.source.sharpref.MyLocalProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
object DataSourceModule {

    @Provides
    fun provideContact(): Contact { return Contact() }

    @Provides
    fun provideContract(): Contract { return Contract() }

    @Provides
    fun provideMyRemoteData(): MyRemoteProfile { return MyRemoteProfile() }

    @Provides
    fun provideMyLocalData(@ApplicationContext context: Context): MyLocalProfile { return MyLocalProfile(context) }

    @Provides
    fun provideCurrencies(@ApplicationContext context: Context): Currencies { return Currencies(context) }
}