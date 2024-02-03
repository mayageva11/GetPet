package com.getpet.Model.ModelRoom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.getpet.GetPetApplication
import com.getpet.Model.ModelRoom.Dao.PostDao
import com.getpet.Model.ModelRoom.Dao.UserDao
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.Entities.UserEntity

@Database(entities = [UserEntity::class, PostEntity::class], version = 2, exportSchema = false)
abstract class AppLocalDB : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao

    companion object {
        // Define a singleton instance of the database
        @Volatile private var instance: AppLocalDB? = null;
        private const val DB_NAME = "GET_PET"

        fun getInstance(): AppLocalDB {
            return instance?: synchronized(this) {
                instance?: Room.databaseBuilder(
                    GetPetApplication.getInstance().applicationContext,
                    AppLocalDB::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
    }
}
