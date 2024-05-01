package com.getpet.ViewModel

import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.getpet.Model.JoinedModel.JoinedUserModel

class ProfileViewModel: ViewModel() {
    val usersModel = JoinedUserModel()

    fun getUserByUid(uid :String,  callback: (UserEntity)-> Unit){
        return usersModel.getUserByUid(uid, callback)
    }
}