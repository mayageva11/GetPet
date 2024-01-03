package com.getpet.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.getpet.MainActivity
import com.getpet.databinding.ActivityPrivateAreaBinding
/*import com.google.firebase.auth.FirebaseAuth*/

class PrivateAreaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateAreaBinding
   /* private lateinit var firebaseAuth: FirebaseAuth*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

       /* firebaseAuth = FirebaseAuth.get Instance()*/

     /*   binding.MyUploads.setOnClickListener {
            val intent = Intent(this,MyUploadsActivity::class.java)
            startActivity(intent)
        }*/

        binding.UploadAPet.setOnClickListener {
            val intent = Intent(this,UploadAPetActivity::class.java)
            startActivity(intent)
        }

        binding.BackToHomePage.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

/*        binding.LogOut.setOnClickListener {
            val user = firebaseAuth.currentUser
            if(user !=null){
                firebaseAuth.signOut()
                Toast.makeText(this,"LogOut",Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }*/

  /*      binding.ResetPassword.setOnClickListener {
            val user = firebaseAuth.currentUser
            val password=binding.Password.text.toString()
            user?.updatePassword(password)?.addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(this,"Password Changed!",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                }
            }

        }*/
    }
}
