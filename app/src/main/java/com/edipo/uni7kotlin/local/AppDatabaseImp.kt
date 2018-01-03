package com.edipo.uni7kotlin.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [Credentials::class], exportSchema = false, version = 1)
abstract class AppDatabaseImp : RoomDatabase() {

    abstract val credentialsDAO: CredentialsDAO

}
