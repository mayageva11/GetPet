package com.getpet.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getpet.R
import com.getpet.components.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyUploadsActivity : AppCompatActivity() {
    //reference to the data base
    val db = Firebase.firestore
    // auth
    lateinit var auth: FirebaseAuth
    private lateinit var recyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_uploads)

        // Initialize RecyclerView and layout manager


        //current user
        val user = Firebase.auth.currentUser
        if(user!=null){
            val uid = user.uid
            db.collection("posts").whereEqualTo("uid", uid)
                .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Handle the documents here
                    val postList = mutableListOf<Post>()
                    for (document in task.result!!) {
                        //get the information from firestore
                        val kind = document.getString("kind") ?: ""
                        val age = document.getString("age") ?:""
                        val about = document.getString("about")?:""
                        val phone = document.getString("phone")?:""
                        val location = document.getString("location") ?:""
                        val owner = document.getString("owner")?:""
                        val url = document.getString("imgUrl")?:""

                        // Create a Post object
                        val post = Post(kind, age, about, phone, location, owner, url)
                        postList.add(post)

                        //TODO: show card on screen
                        // Update the adapter with the postList


                    }
                } else {
                    Toast.makeText(this, "failed to show post", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        }

    }

}
