package com.getpet.Model.ModelRoom.Model

import androidx.lifecycle.LiveData
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.ModelRoom.AppLocalDB
import com.getpet.Model.ModelRoom.Dao.PostDao
import java.util.LinkedList


class PostModel {
    fun getAllPosts(): List<PostEntity> {
        return AppLocalDB.getInstance().postDao().getAllPosts()
    }

    fun insertPost(post: PostEntity) {
        val db = AppLocalDB.getInstance().postDao().insertPost(post)
    }

    fun getPostsByUid(uid: String) : List<PostEntity> {
        return AppLocalDB.getInstance().postDao().getPostsByUserId(uid)
    }

}