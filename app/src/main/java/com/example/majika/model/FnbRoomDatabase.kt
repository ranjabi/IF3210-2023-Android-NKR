package com.example.majika.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Fnb::class], version = 1, exportSchema = false)
abstract class FnbRoomDatabase : RoomDatabase() {
    abstract fun fnbDao(): FnbDao

    companion object {
        @Volatile
        private var INSTANCE: FnbRoomDatabase? = null
        fun getDatabase(context: Context): FnbRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder (
                    context.applicationContext,
                    FnbRoomDatabase::class.java,
                    "fnb_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
