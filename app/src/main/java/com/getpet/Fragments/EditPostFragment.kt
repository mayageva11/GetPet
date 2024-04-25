import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.getpet.Model.Entities.PostEntity
import com.getpet.R
import com.getpet.ViewModel.EditPostViewModel
import com.getpet.ViewModel.MyUploadsViewModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage

class EditPostFragment : Fragment() {
    private lateinit var post: PostEntity
    private lateinit var editPostViewModel: EditPostViewModel
    private lateinit var navController: NavController
    private lateinit var imageView: ImageView
    private lateinit var  kindTextView: TextView
    private lateinit var  aboutTextView: TextView
    private lateinit var  ageTextView: TextView
    private lateinit var  ownerTextView: TextView
    private lateinit var  phoneNumberTextView: TextView
    private lateinit var  locationTextView: TextView
    private lateinit var imageUrlRef : String
    private var imageUri: Uri? = null
    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                //upload the image to Firebase Storage
                uploadImage()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.edit_post_fragment, container, false)

        // Get the PostEntity object from the arguments
        post = arguments?.getSerializable("post") as PostEntity

        // Find the views in the layout
        imageView = view.findViewById(R.id.edit_post_image)
        kindTextView = view.findViewById(R.id.edit_kind_of_a_pet)
         aboutTextView = view.findViewById(R.id.edit_about_of_a_pet)
         ageTextView = view.findViewById(R.id.edit_age_of_a_pet)
         ownerTextView = view.findViewById(R.id.edit_owner_of_a_pet)
         phoneNumberTextView = view.findViewById(R.id.edit_phone_of_a_pet)
         locationTextView = view.findViewById(R.id.edit_location_of_a_pet)

        // Set the values to the views
        Glide.with(requireContext())
            .load(post.img)
            .into(imageView)

        kindTextView.text = post.kind
        aboutTextView.text = post.about
        ageTextView.text = post.age
        ownerTextView.text = post.owner
        locationTextView.text = post.location
        phoneNumberTextView.text = post.phone

        val navHostFragment: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        navController = navHostFragment.navController

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editPostViewModel = ViewModelProvider(this)[EditPostViewModel::class.java]
        val uploadImgBtn = view.findViewById<Button>(R.id.edit_upload_pet_img)

        uploadImgBtn.setOnClickListener {

            imagePicker.launch("image/*")
        }
        // Find the submit button
        val submitButton: MaterialButton = view.findViewById(R.id.save_changes_post)
        submitButton.setOnClickListener {
            val editedPost = PostEntity(post.id, imageUrlRef , kindTextView.text.toString(), ageTextView.text.toString(), aboutTextView.text.toString(),
            phoneNumberTextView.text.toString(), locationTextView.text.toString(), ownerTextView.text.toString(), post.uid)
            editPostViewModel.editPost(editedPost) { isSuccessful ->
                if (isSuccessful) {
                    navController.navigate(R.id.action_global_myUploadsFragment)
                } else {

                }
            }
        }

    }
    private fun uploadImage() {
        imageUri?.let {
            val storageReference = FirebaseStorage.getInstance()
                .getReference("imagePosts/${System.currentTimeMillis()}.jpg")
            storageReference.putFile(it).addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                Toast.makeText(context, "Image uploaded", Toast.LENGTH_SHORT).show()
                // Get the download URL of the uploaded image
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()
                    imageUrlRef= imageUrl
                    // Load the image into your ImageView using Glide
                    Glide.with(this).load(imageUrl).into(imageView)
                }.addOnFailureListener {e ->
                    // Handle failed upload
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener { e ->
                // Handle failed upload
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
