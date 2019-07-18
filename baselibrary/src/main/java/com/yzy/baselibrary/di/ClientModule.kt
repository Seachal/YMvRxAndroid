package com.yzy.baselibrary.di

import com.yzy.baselibrary.http.converter.AbnormalConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.yzy.baselibrary.BuildConfig
import com.yzy.baselibrary.http.ssl.SSLManager
import io.rx_cache2.internal.RxCache
import io.victoralbertos.jolyglot.GsonSpeaker
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 *description: ClientModule.
 *@date 2019/7/15
 *@author: yzy.
 */

const val KODEIN_MODULE_CLIENT_TAG = "clientModule"

const val KODEIN_TAG_FILE_RXCACHE = "rxCacheFile"

class ClientModule {

    companion object {
        private const val TIME_OUT = 30L
        val clientModule = Kodein.Module(KODEIN_MODULE_CLIENT_TAG) {

            bind<GsonBuilder>() with singleton {
                GsonBuilder().serializeNulls()
                    .enableComplexMapKeySerialization()
                    .registerTypeAdapter(Double::class.java, JsonSerializer<Double> { src, _, _ ->
                        if (src != null && src.equals(src.toLong())) {
                            return@JsonSerializer JsonPrimitive(src.toLong())//解决科学计数法转换的问题
                        }
                        JsonPrimitive(src)
                    })
            }

            bind<OkHttpClient.Builder>() with singleton { OkHttpClient.Builder() }

            bind<Retrofit.Builder>() with singleton { Retrofit.Builder() }

            bind<File>(KODEIN_TAG_FILE_RXCACHE) with singleton {
                val cacheDirectory = File(instance<File>(KODEIN_TAG_FILE_CACHEDIR), "RxCache")
                if (!cacheDirectory.exists()) {
                    cacheDirectory.mkdirs()
                }
                cacheDirectory
            }

            bind<RxCache>() with singleton {
                RxCache.Builder().persistence(instance(KODEIN_TAG_FILE_RXCACHE), GsonSpeaker())
            }

            bind<OkHttpClient>() with singleton {
                val builder = instance<OkHttpClient.Builder>()
                    .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                val interceptors = instance<List<Interceptor>>()
                interceptors.forEach {
                    builder.addInterceptor(it)
                }
                if (BuildConfig.DEBUG) {//log拦截
                    builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))//log
                    //测试服忽略证书校验
                    builder.sslSocketFactory(
                        SSLManager.createSSLSocketFactory(),
                        SSLManager.createX509TrustManager()
                    )
                    builder.hostnameVerifier { _, _ -> true }
                }
                builder.build()
            }

            bind<Retrofit>() with singleton {
                instance<Retrofit.Builder>()
                    .baseUrl(instance<HttpUrl>())
                    .client(instance())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//使用rxjava
                    .addConverterFactory(AbnormalConverterFactory.create())//使用自定义的解析
                    .build()
            }
        }
    }
}