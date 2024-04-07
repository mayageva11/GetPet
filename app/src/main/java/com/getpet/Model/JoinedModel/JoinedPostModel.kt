package com.getpet.Model.JoinedModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.ModelFireBase.PostFB
import com.getpet.Model.ModelRoom.Model.PostModel
import java.util.LinkedList

class JoinedPostModel {
    companion object {
        var instance: JoinedPostModel = JoinedPostModel()
    }

    private val modelFirebase = PostFB()
    private val modelRoom = PostModel()
    private val allPosts = AllPostLiveData()

     fun getAllPosts(): AllPostLiveData{
         return allPosts
     }

    fun deletePost(post : PostEntity){

    }

    fun getPostsByUid(uid: String): LiveData<List<PostEntity>> {
        val postsLiveData = MutableLiveData<List<PostEntity>>()
        GetPetApplication.getExecutorService().execute {
            val postsByUid = modelRoom.getPostsByUid(uid)
            GetPetApplication.getExecutorService().execute() {
                postsLiveData.postValue(postsByUid)
            }
        }

            modelFirebase.getPostsByUid(uid) { posts: List<PostEntity> ->
                postsLiveData.postValue(posts)
                // insert into Room
                GetPetApplication.getExecutorService().execute {
                    for (post in posts) {
                        modelRoom.insertPost(post)
                    }
                }
        }

        return postsLiveData
    }

    inner class AllPostLiveData: MutableLiveData<List<PostEntity>>() {
        init{
            value = LinkedList<PostEntity>()
        }

        override fun onActive() {
            super.onActive()

            GetPetApplication.getExecutorService().execute{
                val allPosts = modelRoom.getAllPosts()
                postValue(allPosts)
            }

            modelFirebase.getAllPosts{ posts : List<PostEntity> ->
                value = posts

                GetPetApplication.getExecutorService().execute {
                    for (post in posts) {
                        modelRoom.insertPost(post)
                    }
                }
            }
        }
    }
}