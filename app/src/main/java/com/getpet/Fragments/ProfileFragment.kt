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
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.getpet.Constants
import com.getpet.GetPetApplication
import com.getpet.Model.Entities.UserEntity
import com.getpet.Model.ModelRoom.Model.UserModel
import com.getpet.R
import com.getpet.ViewModel.EditProfileViewModel
import com.getpet.ViewModel.ProfileViewModel
import com.getpet.activities.LoginActivity
import com.getpet.components.PrimaryButton
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase


class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var imageView: ImageView
    private lateinit var NameTextView: TextView
    private lateinit var EmailTextView: TextView
    private lateinit var profileViewModel : ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val uid = currentUser!!.uid
        imageView = view.findViewById(R.id.profileImageView)
        NameTextView = view.findViewById(R.id.user_name)
        EmailTextView = view.findViewById(R.id.show_email)

        profileViewModel.getUserByUid(uid){ userEntity ->
            if(userEntity!=null){
                Glide.with(requireContext())
                    .load(userEntity.profileImg)
                    .into(imageView)

                NameTextView.text= userEntity.name
                EmailTextView.text= userEntity.email
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navHostFragment: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        val navController = navHostFragment.navController

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        val current = auth.currentUser
        val editButton: MaterialButton = view.findViewById(com.getpet.R.id.edit_profile_btn)
        //set up listener for edit button
        editButton.setOnClickListener {
            profileViewModel.getUserByUid(current!!.uid){ userEntity ->
                if(userEntity!=null){
                    val user = UserEntity(current!!.uid,userEntity.name
                        ,userEntity.profileImg,userEntity.email)
                    val bundle = Bundle()
                    bundle.putSerializable("users", user)
                    navController.navigate(R.id.action_global_editProfileFragment, bundle)
                }
            }

            }

        //log out button
        val logOutBtn = view.findViewById<Button>(R.id.logout)
        logOutBtn.setOnClickListener {
            auth.signOut()
            val logOutActivityIntent = Intent(context, LoginActivity::class.java)
            startActivity(logOutActivityIntent)
        }
    }
}

//    private fun handleChangePassword() {
//        val newPassword = changePasswordEditText.text.toString().trim()
//        val newConfirmPassword =changeConfirmPasswordEditText.text.toString().trim()
//
//        if(newPassword== null || newConfirmPassword== null){
//            Toast.makeText(context, "please fill all the information", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (newPassword.length < Constants.PASS_MIN_LENGTH) {
//            Toast.makeText(context, "password need to be at least 6 characters",
//                Toast.LENGTH_SHORT).show()
//            return
//        }
//        if(newPassword != newConfirmPassword){
//            Toast.makeText(context, "please make sure that the password in the same",
//                Toast.LENGTH_SHORT).show()
//            return
//        }
//        //get the  current user
//        val user = Firebase.auth.currentUser
//        if(user!=null){
//            user.updatePassword(newPassword)
//            Toast.makeText(context, "success, password changed", Toast.LENGTH_SHORT).show()
//
//        }
//    }






