package com.getpet.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.getpet.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth

class UploadAPetActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    // Get the FirebaseStorage instance
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                //upload the image to Firebase Storage
                uploadImage()

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_a_pet)

        imageView = findViewById(R.id.post_image)

        // Initialize Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference
        //TODO: activate upload image button
        val uploadImgBtn = findViewById<Button>(R.id.upload_pet_img)
        uploadImgBtn.setOnClickListener {

            imagePicker.launch("image/*")
        }

        //TODO: activate go back btn to map page
        val goBackBtn = findViewById<Button>(R.id.go_back_to_map)
        goBackBtn.setOnClickListener {
            //TODO: change to the correct page- map
            val goBackActivityIntent = Intent(applicationContext, PrivateAreaActivity::class.java)
            startActivity(goBackActivityIntent)
        }

        //get the  current user
        val user = Firebase.auth.currentUser

        // save all the information from the user
        val kindEditText: EditText = findViewById(R.id.kind_of_a_pet)
        val ageEditText: EditText = findViewById(R.id.age_of_a_pet)
        val aboutEditText: EditText = findViewById(R.id.about_of_a_pet)
        val phoneEditText: EditText = findViewById(R.id.phone_of_a_pet)
        val locationEditText: EditText = findViewById(R.id.location_of_a_pet)
        val ownerEditText: EditText = findViewById(R.id.owner_of_a_pet)

        //TODO: activate upload button
        val uploadPostBtn = findViewById<Button>(R.id.upload_pet)
        uploadPostBtn.setOnClickListener {
            val kind = kindEditText.text.toString()
            val age = ageEditText.text.toString()
            val about = aboutEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val location = locationEditText.text.toString()
            val owner = ownerEditText.text.toString()
            // validate the information
            if (validate(kind, age, about, phone, location, owner)) {
                // upload a collection to storage
                if (user != null) {
                    val uid = user.uid
                    //create a new document with the user id
                    val docRef = db.collection("posts").document(uid)
                    //make a collection
                    val data = hashMapOf(
                        "kind" to kind,
                        "age" to age,
                        "about" to about,
                        "phone" to phone,
                        "location" to location,
                        "owner" to owner,
                        "uid" to uid
                    )
                    //upload
                    docRef.set(data).addOnSuccessListener {
                        // Document uploaded successfully
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                        //TODO: show the user my uploads activity with his new post

//                         val uploadPostActivityIntent = Intent(applicationContext, MyUploadsActivity::class.java)
//                         startActivity(uploadPostActivityIntent)
                    }.addOnFailureListener { e ->
                        // Handle the failure
                        Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                // Handle the failure
                Toast.makeText(this, "please fill all the info correctly", Toast.LENGTH_SHORT)
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
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                // Get the download URL of the uploaded image
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    // Load the image into your ImageView using Glide
                    Glide.with(this).load(imageUrl).into(imageView)
                }

            }.addOnFailureListener { e ->
                // Handle failed upload

                // TODO: Convert to a string resource.
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
