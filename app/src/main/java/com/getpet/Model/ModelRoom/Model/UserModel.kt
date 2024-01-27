package com.getpet.Model.ModelRoom.Model

import androidx.lifecycle.LiveData
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.ModelRoom.AppLocalDB

class UserModel {
    fun getUserById(uid: String): UserByIdLiveData {
       return UserByIdLiveData(uid)
    }

    inner class UserByIdLiveData(uid: String) : LiveData<UserEntity>() {
        init {
            GetPetApplication.getExecutorService().execute {
                value = AppLocalDB.getInstance().userDao().getUserById(uid)
            }
        }
    }
    fun updateUser(user: UserEntity): UpdateUserLiveData {
        return UpdateUserLiveData(user)
    }
    inner class UpdateUserLiveData( updatedUser: UserEntity) : LiveData<Boolean>() {
        init {
            GetPetApplication.getExecutorService().execute {
                try {
                    AppLocalDB.getInstance().userDao().updateUser(updatedUser)
                    postValue(true)
                } catch (e: Exception) {
                    postValue(false)
                }
            }
        }
    }
    inner class InsertUserLiveData: LiveData<UserEntity> {
        constructor(dataToInsert: UserEntity) {
            AppLocalDB.getInstance().userDao().insert(dataToInsert)
        }
    }

}