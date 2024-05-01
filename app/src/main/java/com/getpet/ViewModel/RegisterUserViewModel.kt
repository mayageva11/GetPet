package com.getpet.ViewModel

import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.getpet.Model.JoinedModel.JoinedUserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterUserViewModel: ViewModel() {
    val UserModel = JoinedUserModel()

    fun register(user : UserEntity, password: String,  callback: (Boolean) -> Unit){
        UserModel.register(user, password, callback)

    }
}