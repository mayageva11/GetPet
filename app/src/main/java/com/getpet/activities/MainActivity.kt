package com.getpet.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.getpet.R
import com.getpet.R.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        auth = Firebase.auth

        val emailEditText: EditText = findViewById(id.EmailText)
        val passwordEditText: EditText = findViewById(id.PasswordText)

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
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this, getString(string.main_label_log_in_success), Toast.LENGTH_SHORT
                    ).show()
                    //TODO: add intent activity to the next activity- to the map
                    val signInActivityIntent = Intent(applicationContext,UploadAPetActivity::class.java)
                    startActivity(signInActivityIntent)
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

        return isEmailValid && isPasswordValid && (isPasswordLongEnough >= 6)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

}