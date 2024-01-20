package com.getpet.ViewModel

import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class UploadAPetViewModel {
    lateinit var auth: FirebaseAuth
    val db = Firebase.firestore


}