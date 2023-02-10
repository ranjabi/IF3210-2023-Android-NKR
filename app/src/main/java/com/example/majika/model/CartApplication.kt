package com.example.majika.model

import android.app.Application

class CartApplication : Application() {
    val database: FnbRoomDatabase by lazy { FnbRoomDatabase.getDatabase(this) }
}
