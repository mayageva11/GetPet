package com.getpet.activities

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.getpet.Constants
import com.getpet.Model.Entities.UserEntity
import com.getpet.R
import com.getpet.ViewModel.RegisterUserViewModel
import com.getpet.ViewModel.UploadPostViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.IOException


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private var imageUri: Uri? = null
    private lateinit var registerUserViewModel: RegisterUserViewModel
    private lateinit var imageUrlRef : String
    private  lateinit var imageView : ImageView

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
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        // Get ViewModel instance
        registerUserViewModel = ViewModelProvider(this)[RegisterUserViewModel::class.java]

        //get the information from the edit text
        val userNameEditText: EditText = findViewById(R.id.User_Name)
        val emailEditText: EditText = findViewById(R.id.Email)
        val passwordEditText: EditText = findViewById(R.id.Password)
        val showPasswordButton: ImageButton = findViewById(R.id.showPasswordButton)
        val confirmPasswordEditText: EditText = findViewById(R.id.Confirm_Password)
        val showConfirmPasswordButton : ImageButton = findViewById(R.id.showConfirmPasswordButton)
         imageView = findViewById(R.id.user_image)
        val registerProfileImgBtn: MaterialButton = findViewById(R.id.register_profile_img)

        registerProfileImgBtn.setOnClickListener {
            imagePicker.launch("image/*")
        }

        showPasswordButton.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(passwordEditText, isPasswordVisible)
            updateButtonDrawable(showPasswordButton, isPasswordVisible)
        }

        showConfirmPasswordButton.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible
            togglePasswordVisibility(confirmPasswordEditText, isConfirmPasswordVisible)
            updateButtonDrawable(showConfirmPasswordButton, isConfirmPasswordVisible)
        }

        val submitBtn = findViewById<Button>(R.id.submit_btn)
        submitBtn.setOnClickListener {
            // get the email and password and name
            val userName = userNameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            //  validate email and password name
            if (validate(email, password, confirmPassword, userName)) {
                // send the information to the register func
                val user = UserEntity("", userName, imageUrlRef , email)
                registerUserViewModel.register(user,password){isSuccessful ->
                    if(isSuccessful){
                        // message of success
                        Toast.makeText(
                            this,
                            "register successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        //go to main activity to sign in
                        val submitActivityIntent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(submitActivityIntent)
                    } else{
                        // show a failure message
                        Toast.makeText(
                            this, getString(R.string.register_label_Error_failure_sign_up),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            } else {
                Toast.makeText(
                    this, getText(R.string.register_label_Error_fill_information),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        val goBackBtn = findViewById<Button>(R.id.goBack_btn)
        goBackBtn.setOnClickListener {
            val goBackActivityIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(goBackActivityIntent)
        }
    }

    private fun uploadImage() {
        imageUri?.let {
            val storageReference = FirebaseStorage.getInstance()
                .getReference("profile_images/${System.currentTimeMillis()}.jpg")
            storageReference.putFile(it).addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                Toast.makeText(this, "Image uploaded", Toast.LENGTH_SHORT).show()
                // Get the download URL of the uploaded image
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    imageUrlRef= imageUrl
                    // Load the image into your ImageView using Glide
                    Glide.with(this).load(imageUrl).into(imageView)
                }.addOnFailureListener {e ->
                    // Handle failed upload
                    Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }.addOnCompleteListener() { e ->
                    Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(this, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validate(
        email: String,
        password: String,
        confirmPassword: String,
        userName: String
    ): Boolean {
        val isEmailValid = isValidEmail(email)
        val isPasswordValid = password.isNotEmpty()
        val isPasswordLongEnough = password.length
        val doPasswordsMatch = password == confirmPassword
        val isUsernameValid = userName.isNotEmpty()

        return isEmailValid && isPasswordValid && doPasswordsMatch && isUsernameValid
                && (isPasswordLongEnough >= Constants.PASS_MIN_LENGTH)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }


    private fun updateButtonDrawable(button: ImageButton, isVisible: Boolean) {
        val drawableId = if (isVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
        button.setImageResource(drawableId)
    }

    // This function is called when the Show Password button is clicked
    fun onShowPasswordClick(view: android.view.View) {
        isPasswordVisible = !isPasswordVisible
        togglePasswordVisibility(findViewById(R.id.Password), isPasswordVisible)
        updateButtonDrawable(findViewById(R.id.showPasswordButton), isPasswordVisible)
    }

    // This function is called when the Show Confirm Password button is clicked
    fun onShowConfirmPasswordClick(view: android.view.View) {
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        togglePasswordVisibility(findViewById(R.id.Confirm_Password), isConfirmPasswordVisible)
        updateButtonDrawable(findViewById(R.id.showConfirmPasswordButton), isConfirmPasswordVisible)
    }
    private fun togglePasswordVisibility(passwordEditText: EditText, isVisible: Boolean) {
        if (isVisible) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        passwordEditText.setSelection(passwordEditText.text.length)
    }





}