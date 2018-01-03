package com.edipo.uni7kotlin.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface CredentialsDAO {

    @Query("SELECT * FROM credentials")
    fun getAll(): List<Credentials>

    @Query("SELECT * FROM credentials WHERE name LIKE :name")
    fun findByName(name: String): Credentials?

    @Query("SELECT * FROM credentials WHERE name LIKE :name AND password LIKE :password LIMIT 1")
    fun findByNamePassword(name: String, password: String): Credentials?

    @Insert
    fun insertAll(vararg credentials: Credentials)

    @Delete
    fun delete(credentials: Credentials)

}
