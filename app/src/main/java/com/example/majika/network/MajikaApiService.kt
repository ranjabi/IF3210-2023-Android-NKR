package com.example.majika.network

import com.example.majika.model.MenuResponse
import com.example.majika.model.PaymentResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Path

// base url example = "http://192.168.0.111:8000/v1/"
private const val BASE_URL = "http://192.168.0.111:8000/v1/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface MajikaApiService {
    @GET("menu/food")
    suspend fun getAllFood(): MenuResponse
    @GET("menu/drink")
    suspend fun getAllDrink(): MenuResponse
    @POST("payment/{code}")
    suspend fun getPaymentStatus(@Path("code") code: String): PaymentResponse
}

object MajikaApi {
    val retrofitService : MajikaApiService by lazy {
        retrofit.create(MajikaApiService::class.java)
    }
}