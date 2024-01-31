package com.getpet.Model.JoinedModel

import androidx.lifecycle.MutableLiveData
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.ModelFireBase.PostFB
import com.getpet.Model.ModelRoom.Model.PostModel
import java.util.LinkedList

class JoinedPostModel {
    companion object {
        public var instance: JoinedPostModel = JoinedPostModel()

    }
    private val modelFirebase = PostFB()
    private val modelRoom = PostModel()

    private val allPosts = AllPostLiveData()
    private val postByUid = PostByUidLiveData()

     fun getAllPosts(): AllPostLiveData{
         return allPosts
     }
    fun getPostsByUid(uid :String): PostByUidLiveData{
        return postByUid
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

    //all the post by uid
    inner class PostByUidLiveData: MutableLiveData<List<PostEntity>>(){
        init{
            value = LinkedList<PostEntity>()
        }

        override fun onActive() {
            super.onActive()
            fun getPostsByUid(uid : String){
                GetPetApplication.getExecutorService().execute{
                    val postsByUid = modelRoom.getPostsByUid(uid)
                    postValue(postsByUid)
                }
                modelFirebase.getPostsByUid(uid) { posts: List<PostEntity> ->
                    value = posts
                    //insert the the room
                    GetPetApplication.getExecutorService().execute {
                        for (post in posts) {
                            modelRoom.insertPost(post)
                        }
                    }
                }
            }
        }

    }
}