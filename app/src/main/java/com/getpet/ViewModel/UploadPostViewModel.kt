package com.getpet.ViewModel

import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.JoinedModel.JoinedPostModel

class UploadPostViewModel : ViewModel() {
    val postsModel = JoinedPostModel()
    fun uploadPost(post : PostEntity, callback: (Boolean) -> Unit){
        postsModel.uploadPost(post, callback)
    }
}