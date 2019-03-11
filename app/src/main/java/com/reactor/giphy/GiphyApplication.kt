package com.reactor.giphy

import android.app.Application
import com.reactor.giphy.data.Repository
import com.reactor.giphy.data.net.RetrofitGiphyService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GiphyApplication : Application() {

    lateinit var appModule: Module

    override fun onCreate() {
        super.onCreate()

        appModule = module {

            single<RetrofitGiphyService> {
                val okHttpClient = OkHttpClient.Builder()
                if (BuildConfig.DEBUG) {
                    val logInterceptor = HttpLoggingInterceptor()
                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                    okHttpClient.addInterceptor(logInterceptor)
                }

                Retrofit.Builder()
                    .client(okHttpClient.build())
                    .baseUrl("https://api.giphy.com")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(RetrofitGiphyService::class.java)
            }

            single {
                Repository(get())
            }
        }

        startKoin(this, listOf(appModule))
    }
}