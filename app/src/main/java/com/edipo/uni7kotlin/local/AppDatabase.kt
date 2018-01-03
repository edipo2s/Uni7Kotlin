package com.edipo.uni7kotlin.local

import android.arch.persistence.room.Room
import android.content.Context

object AppDatabase {

    private lateinit var databaseImp: AppDatabaseImp

    fun database(context: Context): AppDatabaseImp {
        if(!this::databaseImp.isInitialized){
            databaseImp = Room.databaseBuilder(context, AppDatabaseImp::class.java, "uni7bd").build()
        }
        return databaseImp
    }

}