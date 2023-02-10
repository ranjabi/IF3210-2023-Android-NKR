package com.example.majika.model

import com.squareup.moshi.Json

data class MenuItem (
    @Json(name = "name") val name: String,
    @Json(name = "description") val description: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "price") val price: String,
    @Json(name = "sold") val sold: String,
    @Json(name = "type") val type: String
)