package com.getpet.ViewModel

import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.getpet.Model.JoinedModel.JoinedUserModel

class EditProfileViewModel: ViewModel() {
    val userModel = JoinedUserModel()
    fun editProfile(user : UserEntity, password : String, callback: (Boolean) -> Unit){
        userModel.editProfile(user,password,callback)

    }
}