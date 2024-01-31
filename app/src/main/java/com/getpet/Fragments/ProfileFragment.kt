package com.getpet.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.getpet.Constants
import com.getpet.R
import com.getpet.activities.LoginActivity
import com.getpet.components.PrimaryButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {
    private lateinit var changeConfirmPasswordEditText: EditText
    private lateinit var changePasswordEditText: EditText
    private lateinit var resetPasswordButton: PrimaryButton
    private lateinit var auth: FirebaseAuth
    private var isChangePasswordTextVisible = false
    private var isChangeConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()


        //log out button
        val logOutBtn = view.findViewById<Button>(R.id.logout)
        logOutBtn.setOnClickListener{
            auth.signOut()
            val logOutActivityIntent = Intent(context, LoginActivity::class.java)
            startActivity(logOutActivityIntent)
        }


        // Find views
        changePasswordEditText = view.findViewById(R.id.change_password)
        changeConfirmPasswordEditText = view.findViewById(R.id.change_confirm_password)
        val showChangePasswordButton: ImageButton = view.findViewById(R.id.showChangePasswordButton)
        val showChangeConfirmPasswordButton : ImageButton = view.findViewById(R.id.showChangeConfirmPasswordButton)
        resetPasswordButton = view.findViewById(R.id.reset_password_btn)

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
            handleChangePassword()
        }
    }

    private fun handleChangePassword() {
        val newPassword = changePasswordEditText.text.toString().trim()
        val newConfirmPassword =changeConfirmPasswordEditText.text.toString().trim()

        if(newPassword== null || newConfirmPassword== null){
            Toast.makeText(context, "please fill all the information", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < Constants.PASS_MIN_LENGTH) {
            Toast.makeText(context, "password need to be at least 6 characters",
                Toast.LENGTH_SHORT).show()
            return
        }
        if(newPassword != newConfirmPassword){
            Toast.makeText(context, "please make sure that the password in the same",
                Toast.LENGTH_SHORT).show()
            return
        }
        //get the  current user
        val user = Firebase.auth.currentUser
        if(user!=null){
            user.updatePassword(newPassword)
            Toast.makeText(context, "success, password changed", Toast.LENGTH_SHORT).show()

        }
    }
    private fun updateButtonDrawable(button: ImageButton, isVisible: Boolean) {
        val drawableId = if (isVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
        button.setImageResource(drawableId)
    }

    // This function is called when the Show Password button is clicked
    fun onShowPasswordClick(view: android.view.View) {
        isChangePasswordTextVisible  = !isChangePasswordTextVisible
        togglePasswordVisibility(view.findViewById(R.id.Password), isChangePasswordTextVisible )
        updateButtonDrawable(view.findViewById(R.id.showPasswordButton), isChangePasswordTextVisible )
    }

    // This function is called when the Show Confirm Password button is clicked
    fun onShowConfirmPasswordClick(view: android.view.View) {
        isChangeConfirmPasswordVisible = !isChangeConfirmPasswordVisible
        togglePasswordVisibility(view.findViewById(R.id.Confirm_Password), isChangeConfirmPasswordVisible)
        updateButtonDrawable(view.findViewById(R.id.showConfirmPasswordButton), isChangeConfirmPasswordVisible)
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