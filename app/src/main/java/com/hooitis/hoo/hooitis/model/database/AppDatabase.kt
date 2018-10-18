package com.hooitis.hoo.hooitis.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.hooitis.hoo.hooitis.model.hooitis.Versions
import com.hooitis.hoo.hooitis.model.hooitis.VersionsDao

@Database(entities = [Versions::class], version = 1)
//@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun versionsDao(): VersionsDao
//    abstract fun userDao(): UserDao
//    abstract fun foodDao(): FoodDao
//    abstract fun dietDao(): DietDao
}
