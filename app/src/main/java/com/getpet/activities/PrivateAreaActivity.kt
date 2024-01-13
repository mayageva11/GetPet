package com.getpet.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.getpet.Constants
import com.getpet.R
import com.getpet.components.PrimaryButton
import com.getpet.databinding.ActivityPrivateAreaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class PrivateAreaActivity : AppCompatActivity() {
    private lateinit var changeConfirmPasswordEditText: EditText
    private lateinit var changePasswordEditText: EditText
    private lateinit var resetPasswordButton: PrimaryButton
    private lateinit var auth: FirebaseAuth
    private var isChangePasswordTextVisible = false
    private var isChangeConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_private_area)

        // Initialize Firebase Authentication
         auth = FirebaseAuth.getInstance()

        //my uploads button
        val myUploadsBtn = findViewById<Button>(R.id.transfer_to_my_upload_page)
        myUploadsBtn.setOnClickListener{
            //val MyUploadsIntent = Intent(applicationContext, MyUploadsActivity::class.java)
           // startActivity(MyUploadsIntent)
        }
        //upload a pet button
        val uploadAPetBtn= findViewById<Button>(R.id.transfer_to_upload_a_pet_page)
        uploadAPetBtn.setOnClickListener{
            val uploadAPetActivityIntent = Intent(applicationContext, UploadAPetActivity::class.java)
            startActivity(uploadAPetActivityIntent)
        }

        //log out button
        val logOutBtn = findViewById<Button>(R.id.logout)
        logOutBtn.setOnClickListener{
            val logOutActivityIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(logOutActivityIntent)
        }
        // go back to home page - map page
        val goBackToHomePageBtn = findViewById<Button>(R.id.private_area_go_back)
        goBackToHomePageBtn.setOnClickListener{
            //val goBackToHomePageActivityIntent = Intent(applicationContext, MapActivity::class.java)
            //startActivity(goBackToHomePageActivityIntent)
        }

        // Find views
        changePasswordEditText = findViewById(R.id.change_password)
        changeConfirmPasswordEditText = findViewById(R.id.change_confirm_password)
        val showChangePasswordButton: ImageButton = findViewById(R.id.showChangePasswordButton)
        val showChangeConfirmPasswordButton :ImageButton = findViewById(R.id.showChangeConfirmPasswordButton)
        resetPasswordButton = findViewById(R.id.reset_password_btn)

        showChangePasswordButton.setOnClickListener {
            isChangePasswordTextVisible = ! isChangePasswordTextVisible
            togglePasswordVisibility(changePasswordEditText,  isChangePasswordTextVisible)
            updateButtonDrawable(showChangePasswordButton, isChangePasswordTextVisible)
        }

        showChangeConfirmPasswordButton.setOnClickListener {
            isChangeConfirmPasswordVisible = !isChangeConfirmPasswordVisible
            togglePasswordVisibility(changeConfirmPasswordEditText, isChangeConfirmPasswordVisible)
            updateButtonDrawable(showChangeConfirmPasswordButton, isChangeConfirmPasswordVisible)
        }

        // Set onClickListener for resetPasswordButton
        resetPasswordButton.setOnClickListener {
            handleChangeEmailPassword()
        }
    }
    private fun handleChangeEmailPassword() {
        val newPassword = changePasswordEditText.text.toString().trim()
        val newConfirmPassword =changeConfirmPasswordEditText.text.toString().trim()

        if(newPassword== null || newConfirmPassword== null){
            Toast.makeText(this, "please fill all the information", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < Constants.PASS_MIN_LENGTH) {
            Toast.makeText(this, "password need to be at least 6 characters",
                Toast.LENGTH_SHORT).show()
            return
        }
        if(newPassword != newConfirmPassword){
            Toast.makeText(this, "please make sure that the password in the same",
                Toast.LENGTH_SHORT).show()
            return
        }
        //get the  current user
        val user = Firebase.auth.currentUser
        if(user!=null){
            user.updatePassword(newPassword)
            Toast.makeText(this, "success, password changed", Toast.LENGTH_SHORT).show()

        }
    }
    private fun updateButtonDrawable(button: ImageButton, isVisible: Boolean) {
        val drawableId = if (isVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
        button.setImageResource(drawableId)
    }

    // This function is called when the Show Password button is clicked
    fun onShowPasswordClick(view: android.view.View) {
        isChangePasswordTextVisible  = !isChangePasswordTextVisible
        togglePasswordVisibility(findViewById(R.id.Password), isChangePasswordTextVisible )
        updateButtonDrawable(findViewById(R.id.showPasswordButton), isChangePasswordTextVisible )
    }

    // This function is called when the Show Confirm Password button is clicked
    fun onShowConfirmPasswordClick(view: android.view.View) {
        isChangeConfirmPasswordVisible = !isChangeConfirmPasswordVisible
        togglePasswordVisibility(findViewById(R.id.Confirm_Password), isChangeConfirmPasswordVisible)
        updateButtonDrawable(findViewById(R.id.showConfirmPasswordButton), isChangeConfirmPasswordVisible)
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
