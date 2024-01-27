package com.getpet.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.getpet.Fragments.MapFragment
import com.getpet.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        setUpNav()



    }
    private fun setUpNav(){

        //get instance bottom nav
        val bottomNav : BottomNavigationView = findViewById(R.id.bottom_navigation)

        // set up the navigation controller
        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        val navController = navHostFragment.navController

        //TODO: react to button push in the bottom navigation using the controller
        bottomNav.setOnItemSelectedListener {
            when(it.itemId){

                R.id.profile_fragment -> {
                    navController.navigate(R.id.action_global_profileFragment)
                }
                R.id.upload_a_pet_fragment -> {
                    navController.navigate(R.id.action_global_uploadAPetFragment)
                }
                R.id.my_post_fragment -> {
                    navController.navigate(R.id.action_global_myUploadsFragment)
                }
                R.id.map_fragment-> {
                    navController.navigate(R.id.action_global_mapFragment)
                }
            }

            true
        }
    }

}



