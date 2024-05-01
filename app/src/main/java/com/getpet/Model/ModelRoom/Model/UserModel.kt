package com.getpet.Model.ModelRoom.Model

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.ModelRoom.AppLocalDB

class UserModel {

    fun insert(user: UserEntity){
        val db = AppLocalDB.getInstance().userDao().insert(user)
    }
    fun getUserById(uid: String): UserEntity {
        return AppLocalDB.getInstance().userDao().getUserById(uid)
    }

    fun updateUser(user: UserEntity){
        return AppLocalDB.getInstance().userDao().updateUser(user)
    }


}