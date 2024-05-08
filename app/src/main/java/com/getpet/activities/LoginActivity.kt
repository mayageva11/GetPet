package com.getpet.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getpet.Constants
import com.getpet.R
import com.getpet.R.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    private var isPasswordTextVisible = false
    @SuppressLint("MissingInflatedId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_login)

        auth = Firebase.auth

        if (auth.currentUser != null){
            val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
            startActivity(mainActivityIntent)
        }

        val emailEditText: EditText = findViewById(id.EmailText)
        val passwordEditText: EditText = findViewById(id.PasswordText)
        val showPasswordTextButton: ImageButton = findViewById(R.id.showPasswordTextButton)

        // Set the default state to invisible
        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        showPasswordTextButton.setOnClickListener {
            isPasswordTextVisible = !isPasswordTextVisible
            togglePasswordVisibility(passwordEditText, isPasswordTextVisible)
            updateButtonDrawable(showPasswordTextButton, isPasswordTextVisible)
        }

        val logInButton = findViewById<Button>(id.LogIn_Btn)
        logInButton.setOnClickListener {
            //get the email and password
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            // validate the information
            if (validateSignIn(email, password)) {
                signIn(email, password)
            } else {
                Toast.makeText(
                    this,
                    getString(string.register_label_Error_fill_information),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val registerBtn = findViewById<Button>(id.register_btn)
        registerBtn.setOnClickListener {
            val registerActivityIntent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(registerActivityIntent)
        }

    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                // add intent activity to the next activity
                val mainActivityIntent =Intent(applicationContext, MainActivity::class.java)
                startActivity(mainActivityIntent)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(
                    this, getString(string.main_label_failed_log_in), Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateSignIn(email: String, password: String): Boolean {
        val isEmailValid = isValidEmail(email)
        val isPasswordValid = password.isNotEmpty()
        val isPasswordLongEnough = password.length

        return isEmailValid && isPasswordValid && (isPasswordLongEnough >= Constants.PASS_MIN_LENGTH)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
    private fun togglePasswordVisibility(passwordEditText: EditText, isVisible: Boolean) {
        if (isVisible) {
            passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun updateButtonDrawable(button: ImageButton, isVisible: Boolean) {
        val drawableId = if (isVisible) R.drawable.ic_show_password else R.drawable.ic_hide_password
        button.setImageResource(drawableId)
    }

    // This function is called when the Show Password Text button is clicked
    fun onShowPasswordTextClick(view: android.view.View) {
        isPasswordTextVisible = !isPasswordTextVisible
        togglePasswordVisibility(findViewById(R.id.PasswordText), isPasswordTextVisible)
        updateButtonDrawable(findViewById(R.id.showPasswordTextButton), isPasswordTextVisible)
    }

}