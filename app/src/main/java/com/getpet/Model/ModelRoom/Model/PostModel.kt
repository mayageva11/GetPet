package com.getpet.Model.ModelRoom.Model

import androidx.lifecycle.LiveData
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.ModelRoom.AppLocalDB


class PostModel {

    fun getAllPosts(): AllPostsLiveData {
        return AllPostsLiveData()
    }

    inner class AllPostsLiveData: LiveData<List<PostEntity>> {
        constructor() {
            GetPetApplication.getExecutorService().execute{
                value = AppLocalDB.getInstance().postDao().getAllPosts()
            }
        }
    }

    inner class InsertPostLiveData: LiveData<PostEntity> {
        constructor(dataToInsert: PostEntity) {
            AppLocalDB.getInstance().postDao().insertPost(dataToInsert)
        }
    }

    fun getPostsByUserId(userId: String): PostByUidLiveData {
        return PostByUidLiveData(userId)
    }

    inner class PostByUidLiveData(uid: String) : LiveData<List<PostEntity>>() {
        init {
            GetPetApplication.getExecutorService().execute {
                value = AppLocalDB.getInstance().postDao().getPostsByUserId(uid)
            }
        }
    }

    inner class UpdatePostLiveData(private val updatedPost: PostEntity) : LiveData<Boolean>() {
        init {
            GetPetApplication.getExecutorService().execute {
                try {
                    AppLocalDB.getInstance().postDao().updatePost(updatedPost)
                    postValue(true)
                } catch (e: Exception) {
                    postValue(false)
                }
            }
        }
    }

    fun updatePost(post: PostEntity): UpdatePostLiveData {
        return UpdatePostLiveData(post)
    }
    inner class DeletePostLiveData(private val post: PostEntity) : LiveData<Boolean>() {
        init {
            GetPetApplication.getExecutorService().execute {
                try {
                    AppLocalDB.getInstance().postDao().deletePost(post)
                    postValue(true)
                } catch (e: Exception) {
                    postValue(false)
                }
            }
        }
    }
    fun deletePost(post: PostEntity): DeletePostLiveData {
        return DeletePostLiveData(post)
    }
}