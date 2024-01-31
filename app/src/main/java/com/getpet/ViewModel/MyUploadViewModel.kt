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
    private val _userPosts = MutableLiveData<List<PostEntity>>()
    val userPosts: LiveData<List<PostEntity>> get() = _userPosts
    private val user= auth.currentUser
    val uid= user!!.uid

    fun getUserPosts(): JoinedPostModel.PostByUidLiveData {
        // Observe LiveData from PostViewModel and update MutableLiveData
        return postsModel.getPostsByUid(uid)
    }
    // Method to set user posts in the LiveData
    fun setUserPosts(posts: List<PostEntity>) {
        _userPosts.value = posts
    }
}
