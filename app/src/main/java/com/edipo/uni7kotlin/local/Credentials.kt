package com.edipo.uni7kotlin.local

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class Credentials(@field:PrimaryKey val name: String, val password: String)
