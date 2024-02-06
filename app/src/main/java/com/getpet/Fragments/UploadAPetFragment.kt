package com.getpet.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.getpet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.getpet.utilities.LocationUtils

class UploadAPetFragment : Fragment() {


    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    // Get the FirebaseStorage instance
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var imageUrlRef : String


    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                //upload the image to Firebase Storage
                uploadImage()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload_a_pet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //call view model

        imageView = view.findViewById(R.id.post_image)

        // Initialize Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference
        //upload button
        val uploadImgBtn = view.findViewById<Button>(R.id.upload_pet_img)
        uploadImgBtn.setOnClickListener {

            imagePicker.launch("image/*")
        }

        //get the  current user
        val user = Firebase.auth.currentUser

        // save all the information from the user
        val kindEditText: EditText = view.findViewById(R.id.kind_of_a_pet)
        val ageEditText: EditText = view.findViewById(R.id.age_of_a_pet)
        val aboutEditText: EditText = view.findViewById(R.id.about_of_a_pet)
        val phoneEditText: EditText = view.findViewById(R.id.phone_of_a_pet)
        val locationEditText: EditText = view.findViewById(R.id.location_of_a_pet)
        val ownerEditText: EditText = view.findViewById(R.id.owner_of_a_pet)

        // activate upload button
        val uploadPostBtn = view.findViewById<Button>(R.id.upload_pet)
        uploadPostBtn.setOnClickListener {
            val kind = kindEditText.text.toString()
            val age = ageEditText.text.toString()
            val about = aboutEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val location = locationEditText.text.toString()
            val owner = ownerEditText.text.toString()


            val geoPoint = LocationUtils.convertLocationToGeoPoint(requireContext(), location)

            val image = imageUrlRef
            // validate the information
            if (validate(kind, age, about, phone, location, owner)) {
                // upload a collection to storage
                if (user != null) {
                    val uid = user.uid
                    //create a new document with the user id
                    val docRef = db.collection("posts").document()
                    //make a collection
                    val data = hashMapOf(
                        "kind" to kind,
                        "age" to age,
                        "about" to about,
                        "phone" to phone,
                        "location" to geoPoint,
                        "owner" to owner,
                        "uid" to uid,
                        "image" to image
                    )
                    //upload
                    docRef.set(data).addOnSuccessListener {
                        // Document uploaded successfully
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                        //show the user my uploads activity with his new post
                         val uploadPostActivityIntent = Intent(context, MyUploadsFragment::class.java)
                         startActivity(uploadPostActivityIntent)
                    }.addOnFailureListener { e ->
                        // Handle the failure
                        Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                // Handle the failure
                Toast.makeText(context, "please fill all the info correctly", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun validate(
        kind: String, age: String, about: String, phone: String, location: String, owner: String

    ): Boolean {
        val isKindValid = kind.isNotEmpty()
        val isAgeValid = age.isNotEmpty()
        val isAgeBiggerThen0 = age.toFloat()
        val isAboutValid = about.isNotEmpty()
        val isPhoneValid = phone.isNotEmpty()
        val isPhoneLongEnough = phone.length
        val isLocationValid = location.isNotEmpty()
        val isOwnerValid = owner.isNotEmpty()


        return isKindValid && isAgeValid && isAgeBiggerThen0 > 0 && isAboutValid && isPhoneValid && isPhoneLongEnough == 10 && isLocationValid && isOwnerValid
    }

    private fun uploadImage() {
        imageUri?.let {
            val storageReference = FirebaseStorage.getInstance()
                .getReference("imagePosts/${System.currentTimeMillis()}.jpg")
            storageReference.putFile(it).addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                // Get the download URL of the uploaded image
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    imageUrlRef= imageUrl
                    // Load the image into your ImageView using Glide
                    Glide.with(this).load(imageUrl).into(imageView)
                }.addOnFailureListener {e ->
                    // Handle failed upload
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

