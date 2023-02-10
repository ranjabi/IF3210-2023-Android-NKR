package com.example.majika.network

import com.example.majika.model.MenuResponse
import retrofit2.Retrofit
import retrofit2.http.GET
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

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
    @GET("menu")
    suspend fun getAllMenu(): MenuResponse
}

object MajikaApi {
    val retrofitService : MajikaApiService by lazy {
        retrofit.create(MajikaApiService::class.java)
    }
}