package com.getpet.Fragments

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.getpet.Model.Entities.PostEntity
import com.getpet.R
import com.getpet.ViewModel.UploadPostViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import com.google.android.libraries.places.api.model.Place


class UploadAPetFragment : Fragment() {
    private lateinit var uploadPostViewModel: UploadPostViewModel
    private lateinit var navController: NavController
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore

    // Get the FirebaseStorage instance
    private lateinit var storageRef: StorageReference
    private var imageUri: Uri? = null
    private lateinit var imageView: ImageView
    private lateinit var imageUrlRef: String
    private lateinit var locationTextView: TextView
    private lateinit var kindEditText: Spinner
    private lateinit var ageEditText: EditText
    private lateinit var aboutEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var ownerEditText: EditText

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
        val navHostFragment: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        navController = navHostFragment.navController
        val view = inflater.inflate(R.layout.fragment_upload_a_pet, container, false)


        allDogsKind();

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationTextView =view.findViewById(R.id.locationText)


        if (!Places.isInitialized()){
            Places.initialize(requireContext(), "AIzaSyCU8AHeARmXXMQvEimvqaq11w3xqvyodgM")
        }
        val autocompleteSupportFragment=(childFragmentManager.findFragmentById(R.id.location_of_a_pet) as AutocompleteSupportFragment).setPlaceFields(
            listOf(Place.Field.LAT_LNG,Place.Field.NAME)

        )
        autocompleteSupportFragment.setHint("Select Location")

        autocompleteSupportFragment.setOnPlaceSelectedListener(object :PlaceSelectionListener{
            override fun onError(p0: Status) {
                Log.e( "error",p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {
                if(p0.latLng!=null){
                    val locationName = p0.name ?: ""
                    locationTextView.text = Editable.Factory.getInstance().newEditable(locationName)



                    Toast.makeText(requireContext(), locationName.toString(), Toast.LENGTH_SHORT).show()
                }
            }


        })
        // Initialize ViewModel instance
        uploadPostViewModel = ViewModelProvider(this)[UploadPostViewModel::class.java]

        // Initialize ImageView for displaying selected image
        imageView = view.findViewById(R.id.post_image)

        // Initialize Firebase Storage
        storageRef = FirebaseStorage.getInstance().reference

        // Initialize image picker
        val uploadImgBtn = view.findViewById<Button>(R.id.upload_pet_img)
        uploadImgBtn.setOnClickListener {
            imagePicker.launch("image/*")
        }

        // Get the current user
        val user = Firebase.auth.currentUser

        // Initialize views for pet details
        kindEditText = view.findViewById(R.id.spinner_kind_of_a_pet)
        ageEditText = view.findViewById(R.id.age_of_a_pet)
        aboutEditText = view.findViewById(R.id.about_of_a_pet)
        phoneEditText = view.findViewById(R.id.phone_of_a_pet)
        ownerEditText = view.findViewById(R.id.owner_of_a_pet)


        // Handle upload button click
        val uploadPostBtn = view.findViewById<Button>(R.id.upload_pet)
        uploadPostBtn.setOnClickListener {
            val kind = kindEditText.selectedItem.toString()
            val age = ageEditText.text.toString()
            val about = aboutEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val location = locationTextView.text.toString()
            val owner = ownerEditText.text.toString()

            // Validate pet details
            if (validate(kind, age, about, phone, location, owner)) {
                // Upload post to Firebase
                if (user != null) {
                    val uid = user.uid
                    val post = PostEntity("", imageUrlRef, kind, age, about, phone, location, owner, uid)
                    uploadPostViewModel.uploadPost(post) { isSuccessful ->
                        if (isSuccessful) {
                            Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT).show()
                            // Navigate to MyUploadsFragment
                            navController.navigate(R.id.action_global_myUploadsFragment)
                        } else {
                            Toast.makeText(context, "Upload failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Display error message if validation fails
                Toast.makeText(context, "Please fill in all the information correctly", Toast.LENGTH_SHORT).show()
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
                    imageUrlRef = imageUrl
                    // Load the image into your ImageView using Glide
                    Glide.with(this).load(imageUrl).into(imageView)
                }.addOnFailureListener { e ->
                    // Handle failed upload
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }

            }.addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun allDogsKind() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://dogbreeddb.p.rapidapi.com/")
            .get()
            .addHeader("X-RapidAPI-Key", "2cb0328d76msh1e2591f5b72da78p137ae2jsnfd81ab8cb02b")
            .addHeader("X-RapidAPI-Host", "dogbreeddb.p.rapidapi.com")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                activity?.runOnUiThread {
                    val breedNamesArray = extractBreedNames(responseData)
                    val defaultWord = getString(R.string.upload_a_pet_kind_spinner)
                    val breedNamesWithDefaultWordArr = arrayOf(defaultWord) + breedNamesArray
                    val spinnerKindOfPet: Spinner? = view?.findViewById(R.id.spinner_kind_of_a_pet)
                    val adapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        breedNamesWithDefaultWordArr
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
// Apply the adapter to the spinner
                    spinnerKindOfPet?.adapter = adapter
                    spinnerKindOfPet?.setSelection(0, false) // Set the initial selection to "Select A Kind"
                }
            }
        })
    }
    private fun extractBreedNames(responseData: String?): Array<String> {
        if (responseData.isNullOrEmpty()) return emptyArray()
        val jsonArray = JSONArray(responseData)
        val breedNamesList = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val breedName = jsonObject.getString("breedName")
            breedNamesList.add(breedName)
        }
        return breedNamesList.toTypedArray()
    }
}