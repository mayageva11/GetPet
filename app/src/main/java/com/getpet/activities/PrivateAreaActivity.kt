package com.getpet.activities


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.getpet.databinding.ActivityPrivateAreaBinding

class PrivateAreaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivateAreaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateAreaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.UploadAPet.setOnClickListener {
            val intent = Intent(this,UploadAPetActivity::class.java)
            startActivity(intent)
        }

        binding.BackToHomePage.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
