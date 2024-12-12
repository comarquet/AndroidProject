package com.automacorp.service

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.security.SecureRandom
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate


object ApiServices {

    const val API_USERNAME = "user"
    const val API_PASSWORD = "password"

    val roomsApiService : RoomsApiService by lazy {
        val client = getUnsafeOkHttpClient()
            .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
            .build()

        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .baseUrl("http://192.168.1.124:8080/api/") // (2)
            .build()
            .create(RoomsApiService::class.java)
    }

    val windowsApiService: WindowsApiService by lazy {
        val client = getUnsafeOkHttpClient()
            .addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
            .build()

        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .baseUrl("http://192.168.1.124:8080/api/") // (2)
            .build()
            .create(WindowsApiService::class.java)
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            val trustManager = object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
            val sslContext = SSLContext.getInstance("SSL").also {
                it.init(null, arrayOf(trustManager), SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustManager)
            hostnameVerifier { hostname, _ -> hostname.contains("localhost") }
            addInterceptor(BasicAuthInterceptor(API_USERNAME, API_PASSWORD))
        }
}

class BasicAuthInterceptor(val username: String, val password: String): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain
            .request()
            .newBuilder()
            .header("Authorization", Credentials.basic(username, password))
            .build()
        return chain.proceed(request)
    }
}