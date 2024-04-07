package com.getpet.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.google.firebase.auth.FirebaseAuth

class MyUploadsViewModel : ViewModel() {

    private val postsModel = JoinedPostModel()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // MutableLiveData to hold posts by UID data
    private lateinit var _userPosts: LiveData<List<PostEntity>>;
    val userPosts: LiveData<List<PostEntity>> get() = _userPosts
    private val user= auth.currentUser
    val uid= user!!.uid


    // Method to set user posts in the LiveData
    fun getUserPosts(uid: String) {
        this._userPosts = postsModel.getPostsByUid(uid)
    }
    fun deletePost(post: PostEntity){
//        this._userPosts = postsModel.deletePost(post)
    }

}
