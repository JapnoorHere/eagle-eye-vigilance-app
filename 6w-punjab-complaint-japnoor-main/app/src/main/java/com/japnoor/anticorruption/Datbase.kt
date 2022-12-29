package com.japnoor.anticorruption

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ComplaintsEntity::class,SignUpEntity::class,SecurityQuestionEntity::class,DemandEntity::class], version = 4)
abstract class Datbase : RoomDatabase() {
    abstract fun dao(): Daao

    companion object {
        var database: Datbase? = null


        @Synchronized
        fun getDatabase(context: Context): Datbase {
            if (database == null) {
                database =
                        Room.databaseBuilder(context, Datbase::class.java, "Anti Corruption Helpline")
                        .build()

            }
            return database!!
        }

    }

}