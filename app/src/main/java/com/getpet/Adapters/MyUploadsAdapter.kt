package com.getpet.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.getpet.Model.Entities.PostEntity
import com.getpet.R
import com.google.android.material.button.MaterialButton

class MyUploadsAdapter :
    RecyclerView.Adapter<MyUploadsAdapter.MyUploadsViewHolder>() {

    private var posts = emptyList<PostEntity>()

        inner class MyUploadsViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
//            private val deleteButton: MaterialButton = itemView.findViewById(R.id.delete_btn)

            private val image : ImageView = itemView.findViewById(R.id.my_post_image)
            private val kindTextView: TextView = itemView.findViewById(R.id.my_post_kind)
            private val ageTextView: TextView = itemView.findViewById(R.id.my_post_age)
            private val aboutTextView: TextView = itemView.findViewById(R.id.my_post_about)
            private val locationTextView: TextView = itemView.findViewById(R.id.my_post_location)
            private val phoneTextView: TextView = itemView.findViewById(R.id.my_post_phone)
            private val ownerTextView: TextView = itemView.findViewById(R.id.my_post_owner)

//            init {
//                deleteButton.setOnClickListener {
//                    val position = adapterPosition
//                    if (position != RecyclerView.NO_POSITION) {
//                        onDeleteClickListener?.onDeleteClick(position)
//                    }
//                }
//            }

            fun bind(post: PostEntity) {
                // Load image using Glide
                Glide.with(itemView.context).load(post.img).into(image)

                // Set other post details
                kindTextView.text = post.kind
                ageTextView.text = post.age
                aboutTextView.text = post.about
                locationTextView.text = post.location
                phoneTextView.text = post.phone
                ownerTextView.text = post.owner
            }

        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyUploadsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_post_card, parent, false)
        return MyUploadsViewHolder(view)
    }
    override fun onBindViewHolder(holder: MyUploadsViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(post)
    }

    override fun getItemCount(): Int = posts.size


    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newPosts: List<PostEntity>) {
        posts = newPosts
        notifyDataSetChanged()
    }

//    interface OnDeleteClickListener {
//        fun onDeleteClick(position: Int)
//    }
//
//    var onDeleteClickListener: OnDeleteClickListener? = null

}