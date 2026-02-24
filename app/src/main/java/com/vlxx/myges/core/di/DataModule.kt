package com.vlxx.myges.core.di

import com.vlxx.myges.data.constants.APP_CONNECT_TIMEOUT
import com.vlxx.myges.data.constants.APP_READ_TIMEOUT
import com.vlxx.myges.data.network.Api
import com.vlxx.myges.data.network.AuthInterceptor
import com.vlxx.myges.data.network.LoggingInterceptor
import com.vlxx.myges.data.repositories.AgendaRepositoryImpl
import com.vlxx.myges.data.repositories.LocalSettingsRepositoryImpl
import com.vlxx.myges.data.repositories.ProfileRepositoryImpl
import com.vlxx.myges.data.repositories.UserRepositoryImpl
import com.vlxx.myges.domain.repositories.AgendaRepository
import com.vlxx.myges.domain.repositories.LocalSettingsRepository
import com.vlxx.myges.domain.repositories.ProfileRepository
import com.vlxx.myges.domain.repositories.UserRepository
import com.vlxx.myges.ui.screens.authenticated.agenda.viewModel.AgendaViewModel
import com.vlxx.myges.ui.screens.authenticated.profile.viewModel.ProfileViewModel
import com.vlxx.myges.ui.screens.splash.viewModel.SplashViewModel
import com.vlxx.myges.ui.screens.unauthenticated.signInScreen.viewModel.SignInViewModel
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val dataModule = module {

    single { AuthInterceptor() }

    single {
        OkHttpClient.Builder()
            .connectTimeout(APP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(APP_READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(LoggingInterceptor())
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl("https://api.kordis.fr/")
            .client(get())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    single<Api> { get<Retrofit>().create(Api::class.java) }

    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::LocalSettingsRepositoryImpl) { bind<LocalSettingsRepository>() }
    singleOf(::ProfileRepositoryImpl) { bind<ProfileRepository>() }
    singleOf(::AgendaRepositoryImpl) { bind<AgendaRepository>() }

    viewModelOf(::SplashViewModel)
    viewModel { SignInViewModel(get(), get()) }
    viewModelOf(::ProfileViewModel)
    viewModelOf(::AgendaViewModel)

}


