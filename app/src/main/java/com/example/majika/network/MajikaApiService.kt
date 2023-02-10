package com.example.majika.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

// base url example = "http://192.168.0.111:8000/v1/"
private const val BASE_URL = "http://10.5.108.154:3000/v1/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface MajikaApiService {
    @GET("menu")
    suspend fun getAllMenu(): String
}

object MajikaApi {
    val retrofitService : MajikaApiService by lazy {
        retrofit.create(MajikaApiService::class.java)
    }
}