package com.example.majika.model

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = [Fnb::class], version = 3, exportSchema = false)
abstract class FnbRoomDatabase : RoomDatabase() {
    abstract fun fnbDao(): FnbDao

    private class FnbDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("FnbRoomDatabase", "database is created")
            INSTANCE?.let { database ->
                scope.launch {
                    var fnbDao = database.fnbDao()
                    Log.d("FnbRoomDatabase", "callback is launched")

                    val fnb = Fnb(fnbName = "Kecap", fnbPrice = 99999, fnbQuantity = 20)
                    val fnb2 = Fnb(fnbName = "botol", fnbPrice = 11111, fnbQuantity = 2)

                    fnbDao.insert(fnb)
                    fnbDao.insert(fnb2)
                    Log.d("FnbRoomDatabase", "callback is called")
                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: FnbRoomDatabase? = null
        fun getDatabase(context: Context, scope: CoroutineScope): FnbRoomDatabase {
            Log.d("FnbRoomDatabase", "getDatabase is called")
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder (
                    context.applicationContext,
                    FnbRoomDatabase::class.java,
                    "fnb_database"
                ).fallbackToDestructiveMigration().addCallback(FnbDatabaseCallback(scope)).build()
                Log.d("FnbRoomDatabase", "database build success")
                INSTANCE = instance
                return instance
            }
        }
    }
}
