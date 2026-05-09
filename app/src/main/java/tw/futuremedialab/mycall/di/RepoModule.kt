package tw.futuremedialab.mycall.di

import tw.futuremedialab.mycall.data.AuthRepositoryImpl
import tw.futuremedialab.mycall.data.ContactPhotoProviderImpl
import tw.futuremedialab.mycall.data.BlockedNumberRepositoryImpl
import tw.futuremedialab.mycall.domain.repo.AuthRepository
import tw.futuremedialab.mycall.core.CallUiEffects
import tw.futuremedialab.mycall.core.CallUiEffectsHandler
import tw.futuremedialab.mycall.core.DefaultCallOrchestrator
import tw.futuremedialab.mycall.core.DefaultDtmfToneGenerator
import tw.futuremedialab.mycall.core.DefaultNotificationController
import tw.futuremedialab.mycall.data.SimInfoProviderImpl
import tw.futuremedialab.mycall.data.CallLogRepositoryImpl
import tw.futuremedialab.mycall.data.ContactDetailProviderImpl
import tw.futuremedialab.mycall.data.ContactsRepositoryImpl
import tw.futuremedialab.mycall.data.DefaultRingToneController
import tw.futuremedialab.mycall.domain.CallOrchestrator
import tw.futuremedialab.mycall.domain.DtmfToneGenerator
import tw.futuremedialab.mycall.domain.NotificationController
import tw.futuremedialab.mycall.domain.RingToneController
import tw.futuremedialab.mycall.domain.repo.SimInfoProvider
import tw.futuremedialab.mycall.domain.repo.BlockedNumberRepository
import tw.futuremedialab.mycall.domain.repo.CallLogRepository
import tw.futuremedialab.mycall.domain.repo.ContactDetailProvider
import tw.futuremedialab.mycall.domain.repo.ContactPhotoProvider
import tw.futuremedialab.mycall.domain.repo.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {

    @Binds
    @Singleton
    abstract fun bindRingtoneController(
        defaultRingToneController: DefaultRingToneController
    ): RingToneController

    @Binds
    @Singleton
    abstract fun bindNotificationController(
        notificationController: DefaultNotificationController
    ): NotificationController


    @Binds
    @Singleton
    abstract fun bindCallOrchestrator(
        callOrchestrator: DefaultCallOrchestrator
    ): CallOrchestrator

    @Binds
    @Singleton
    abstract fun bindCallUiEffects(
        impl: CallUiEffectsHandler
    ): CallUiEffects

    @Binds
    abstract fun bindCallLogRepo(
        impl: CallLogRepositoryImpl
    ): CallLogRepository

    @Binds
    abstract fun bindContactRepo(
        impl: ContactsRepositoryImpl
    ): ContactRepository

    @Binds
    @Singleton
    abstract fun bindToneGenerator(
        impl: DefaultDtmfToneGenerator
    ): DtmfToneGenerator

    @Binds
    @Singleton
    abstract fun bindContactPhotoProvider(
        impl: ContactPhotoProviderImpl
    ): ContactPhotoProvider

    @Binds
    @Singleton
    abstract fun bindContactDetailProvider(
        impl: ContactDetailProviderImpl
    ): ContactDetailProvider

    @Binds
    @Singleton
    abstract fun bindSimInfoProvider(
        impl: SimInfoProviderImpl
    ): SimInfoProvider

    @Binds
    @Singleton
    abstract fun bindBlockedNumberRepository(
        impl: BlockedNumberRepositoryImpl
    ): BlockedNumberRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}
