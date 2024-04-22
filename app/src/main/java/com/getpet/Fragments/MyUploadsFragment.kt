package com.getpet.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.getpet.Adapters.MyUploadsAdapter
import com.getpet.Model.Entities.PostEntity
import com.getpet.R
import com.getpet.ViewModel.MyUploadsViewModel
import com.google.firebase.auth.FirebaseAuth


class MyUploadsFragment : Fragment() {

    private lateinit var myUploadsViewModel: MyUploadsViewModel
    private lateinit var myAdapter: MyUploadsAdapter
    private lateinit var recyclerView: RecyclerView
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user= auth.currentUser
    val uid= user!!.uid


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_uploads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get ViewModel instance
        myUploadsViewModel = ViewModelProvider(this)[MyUploadsViewModel::class.java]

        // Initialize RecyclerView and adapter
        recyclerView = view.findViewById(R.id.recyclerView)
        myAdapter = MyUploadsAdapter(myUploadsViewModel, activity)
        recyclerView.adapter = myAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch user-specific posts
        fetchUserPosts()
        // Observe user posts
        observeUserPosts()



    }

   private fun observeUserPosts() {
        myUploadsViewModel.userPosts.observe(viewLifecycleOwner) { posts ->
            myAdapter.submitList(posts)
        }
    }
    private fun fetchUserPosts() {
        // Call the getUserPosts function to start observing the LiveData
        myUploadsViewModel.getUserPosts(uid)
    }

    private fun deletePost(post : PostEntity){

        
    }

}