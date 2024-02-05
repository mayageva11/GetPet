package com.getpet.activities

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
import com.getpet.Constants
import com.getpet.R
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.IOException
import com.google.firebase.storage.StorageReference


class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false
    private val PICK_IMAGE_REQUEST = 1
    private var imageUri: Uri? = null
//    private val GALLERY_REQUEST_CODE = 123
//    private lateinit var profileImageView: ImageView
//    private lateinit var profileImgUrl : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        val storage = FirebaseStorage.getInstance()
//        val storageReference: StorageReference = storage.reference.child("profile_images")
//
//        profileImageView= findViewById(R.id.profile_image_display)
//        val selectImageBtn : Button= findViewById(R.id.profile_image)


        //get the information from the edit text
        val userNameEditText: EditText = findViewById(R.id.User_Name)
        val emailEditText: EditText = findViewById(R.id.Email)
        val passwordEditText: EditText = findViewById(R.id.Password)
        val showPasswordButton: ImageButton = findViewById(R.id.showPasswordButton)
        val confirmPasswordEditText: EditText = findViewById(R.id.Confirm_Password)
        val showConfirmPasswordButton : ImageButton = findViewById(R.id.showConfirmPasswordButton)
        val profileImageView: ImageView = findViewById(R.id.user_image)
        val registerProfileImgBtn: MaterialButton = findViewById(R.id.register_profile_img)

        registerProfileImgBtn.setOnClickListener {
            openFileChooser()
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
                register(email, password, userName)
            } else {
                Toast.makeText(
                    this, getText(R.string.register_label_Error_fill_information),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (imageUri != null) {
                // Upload the image to Firebase Storage
                uploadImage()
            }

        }


        val goBackBtn = findViewById<Button>(R.id.goBack_btn)
        goBackBtn.setOnClickListener {
            val goBackActivityIntent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(goBackActivityIntent)
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val profileImageView: ImageView = findViewById(R.id.user_image)
                profileImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver: ContentResolver = contentResolver
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val storage = FirebaseStorage.getInstance()
            val storageReference = storage.reference

            val fileReference =
                storageReference.child("profile_images/" + System.currentTimeMillis() + "." + getFileExtension(
                    imageUri!!
                ))

            fileReference.putFile(imageUri!!)
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    // Handle successful upload
                    // Now you can get the download URL for the image
                    fileReference.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Use downloadUrl in your user registration logic
                        // For example, you can save it to Firebase Database or associate it with the user
                    }
                }
                .addOnFailureListener { e ->
                    // Handle unsuccessful upload
                    Toast.makeText(
                        this@RegisterActivity,
                        "Upload failed: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
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

    private fun register(email: String, password: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // message of success
                    Toast.makeText(
                        this,
                        getString(R.string.register_label_Error_fill_information),
                        Toast.LENGTH_SHORT
                    ).show()
                    //go to main activity to sign in
                    val submitActivityIntent = Intent(applicationContext, LoginActivity::class.java)
                    startActivity(submitActivityIntent)
                } else {
                    // show a failure message
                    Toast.makeText(
                        this, getString(R.string.register_label_Error_failure_sign_up),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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