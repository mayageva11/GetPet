package com.getpet.ViewModel

import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.google.firebase.auth.FirebaseAuth

class EditPostViewModel: ViewModel() {
    val postsModel = JoinedPostModel()
//    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    fun editPost(post : PostEntity, callback: (Boolean) -> Unit){
        postsModel.editPost(post, callback)
    }
}