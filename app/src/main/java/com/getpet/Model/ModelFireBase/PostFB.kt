package com.getpet.Model.ModelFireBase
import com.getpet.Model.Entities.PostEntity
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.LinkedList

class PostFB {
    companion object {
        val COLLECTION_NAME: String = "posts"
    }
    val db = Firebase.firestore
    //get all post from firebase
    fun getAllPosts(callback: (List<PostEntity>) -> Unit) {
        val query: Query = db.collection(COLLECTION_NAME)

        query.get().addOnCompleteListener{snapshot->
            if(snapshot.isSuccessful){
                val list = LinkedList<PostEntity>()
                val doc = snapshot.result

                for(postMap in doc){
                    val post = PostEntity(postMap.id, "", "", "", "", "", "", "", "")
                    post.fromMap(postMap.data)


                    list.add(post)
                }

                callback(list)
            }
        }
    }

    //get all the posts by user id
    fun getPostsByUid(uid: String, callback: (List<PostEntity>) -> Unit) {
        val query: Query = db.collection(COLLECTION_NAME)
            .whereEqualTo("uid", uid)
        query.get().addOnCompleteListener { snapshot ->
            if (snapshot.isSuccessful) {
                val list = mutableListOf<PostEntity>()

                for (document in snapshot.result!!) {
                    val post = PostEntity(document.id, "", "", "", "", "", "", "", "")
                    post.fromMap(document.data)
                    list.add(post)
                }

                callback(list)
            } else {
                val exception = snapshot.exception
                println("Error fetching posts for UID: $uid. Exception: $exception")
                // Handle the case where the task was not successful
                callback(emptyList())
            }
        }
    }

    fun deletePost(post : PostEntity){

    }


}
