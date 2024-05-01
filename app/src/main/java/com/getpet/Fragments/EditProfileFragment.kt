package com.getpet.Fragments

import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.getpet.Model.Entities.UserEntity
import com.getpet.R
import com.getpet.ViewModel.EditProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage

class EditProfileFragment: Fragment() {
    private lateinit var navController: NavController
    private lateinit var user: UserEntity
    private lateinit var editProfileViewModel: EditProfileViewModel
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private lateinit var imageView: ImageView
    private lateinit var  NameTextView: TextView
    private lateinit var  EmailTextView: TextView
    private lateinit var  PasswordTextView: TextView
    private lateinit var  ConfirmPasswordTextView: TextView
    private lateinit var imageUrlRef : String
    private var imageUri: Uri? = null
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
        /// Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        // Get the PostEntity object from the arguments
        user = arguments?.getSerializable("users") as UserEntity
        imageView= view.findViewById(R.id.edit_user_image)
        NameTextView=view.findViewById(R.id.edit_user_Name)
        EmailTextView=view.findViewById(R.id.edit_email)

        // Set the values to the views
        Glide.with(requireContext())
            .load(user.profileImg)
            .into(imageView)

        NameTextView.text = user.name
        EmailTextView.text=user.email
        imageUrlRef= Uri.parse(user.profileImg).toString()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        navController = navHostFragment.navController
        isPasswordVisible = false
        isConfirmPasswordVisible = false
        editProfileViewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
        PasswordTextView = view.findViewById(R.id.edit_Password)
        ConfirmPasswordTextView = view.findViewById(R.id.edit_confirm_Password)
        val saveChangesBtn: MaterialButton = view.findViewById(R.id.save_info_btn)
        val changeImage : MaterialButton = view.findViewById(R.id.edit_profile_img)
        val goBack : MaterialButton = view.findViewById(R.id.cancel_btn)
        val showConfirmPasswordButton : ImageButton = view.findViewById(R.id.showConfirmPasswordButtonEditProfile)
        val showPasswordButton: ImageButton = view.findViewById(R.id.showPasswordButtonEditProfile)

        showPasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            PasswordTextView.transformationMethod = if (isPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            updateButtonDrawable(showPasswordButton, isPasswordVisible)
        }

        showConfirmPasswordButton.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            ConfirmPasswordTextView.transformationMethod = if (isConfirmPasswordVisible) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            updateButtonDrawable(showConfirmPasswordButton, isConfirmPasswordVisible)
        }

        saveChangesBtn.setOnClickListener{
            val userName = NameTextView.text.toString()
            val email = EmailTextView.text.toString()
            val password = PasswordTextView.text.toString()
            val confirmPassword = ConfirmPasswordTextView.text.toString()
            val imageUrl = imageUrlRef


            val newUser = UserEntity(user.uid,userName,imageUrl,email)
            if(!isValidEmail(email)){
                // Email is not valid, show a Toast message and return
                Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(password != confirmPassword){
                Toast.makeText(context, "Please confirm your password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(password.length<6 && password!=""){
                Toast.makeText(context, "password need to be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            editProfileViewModel.editProfile(newUser,password){isSuccessful ->
                if(isSuccessful){
                    Toast.makeText(context, "success! changes are saved", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_global_profileFragment)
                }else{
                    Toast.makeText(context, "something went wrong, try again", Toast.LENGTH_SHORT).show()
                }

            }


        }

        changeImage.setOnClickListener{
            imagePicker.launch("image/*")
        }

        goBack.setOnClickListener{
            navController.navigate(R.id.action_global_profileFragment)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun uploadImage() {
        imageUri?.let {
            val storageReference = FirebaseStorage.getInstance()
                .getReference("profile_images/${System.currentTimeMillis()}.jpg")
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
                }.addOnCompleteListener() { e ->
                    Toast.makeText(context, "Error occurred", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun updateButtonDrawable(button: ImageButton, isVisible: Boolean) {
        val drawableId = if (isVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
        button.setImageResource(drawableId)
    }




}