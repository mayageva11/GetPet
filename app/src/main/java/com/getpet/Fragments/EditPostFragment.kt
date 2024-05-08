import android.app.VoiceInteractor
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
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
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.button.MaterialButton
import com.google.firebase.storage.FirebaseStorage
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class EditPostFragment : Fragment() {
    private lateinit var post: PostEntity
    private lateinit var editPostViewModel: EditPostViewModel
    private lateinit var navController: NavController
    private lateinit var imageView: ImageView
    private lateinit var  kindTextView: Spinner
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
        locationTextView =view.findViewById(R.id.locationText)
        kindTextView = view.findViewById(R.id.spinner_kind_of_a_pet)
        aboutTextView = view.findViewById(R.id.edit_about_of_a_pet)
        ageTextView = view.findViewById(R.id.edit_age_of_a_pet)
        ownerTextView = view.findViewById(R.id.edit_owner_of_a_pet)
        phoneNumberTextView = view.findViewById(R.id.edit_phone_of_a_pet)

        // Set the values to the views
        Glide.with(requireContext())
            .load(post.img)
            .into(imageView)


        aboutTextView.text = post.about
        ageTextView.text = post.age
        ownerTextView.text = post.owner
        locationTextView.text = post.location
        phoneNumberTextView.text = post.phone
        imageUrlRef= Uri.parse(post.img).toString()

        val navHostFragment: NavHostFragment = activity?.supportFragmentManager
            ?.findFragmentById(R.id.main_navhost_frag) as NavHostFragment
        navController = navHostFragment.navController
        allDogsKind();
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationTextView =view.findViewById(R.id.locationText)


        if (!Places.isInitialized()){
            Places.initialize(requireContext(), "AIzaSyCU8AHeARmXXMQvEimvqaq11w3xqvyodgM")
        }
        val autocompleteSupportFragment=(childFragmentManager.findFragmentById(R.id.location_of_a_pet) as AutocompleteSupportFragment).setPlaceFields(
            listOf(Place.Field.LAT_LNG, Place.Field.NAME)

        )
        autocompleteSupportFragment.setHint("Edit Location")

        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onError(p0: Status) {
                Log.e( "error",p0.statusMessage.toString())
            }

            override fun onPlaceSelected(p0: Place) {
                if(p0.latLng!=null){
                    val locationName = p0.name ?: ""
                    locationTextView.text = Editable.Factory.getInstance().newEditable(locationName)



                    Toast.makeText(requireContext(), locationName.toString(), Toast.LENGTH_SHORT).show()
                }
            }


        })

        editPostViewModel = ViewModelProvider(this)[EditPostViewModel::class.java]
        val uploadImgBtn = view.findViewById<Button>(R.id.edit_upload_pet_img)

        uploadImgBtn.setOnClickListener {

            imagePicker.launch("image/*")
        }
        // Find the submit button
        val submitButton: MaterialButton = view.findViewById(R.id.save_changes_post)
        submitButton.setOnClickListener {
            val editedPost = PostEntity(post.id, imageUrlRef , kindTextView.selectedItem.toString(), ageTextView.text.toString(), aboutTextView.text.toString(),
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

    private fun allDogsKind() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://dogbreeddb.p.rapidapi.com/")
            .get()
            .addHeader("X-RapidAPI-Key", "2cb0328d76msh1e2591f5b72da78p137ae2jsnfd81ab8cb02b")
            .addHeader("X-RapidAPI-Host", "dogbreeddb.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                // Update UI with the modified response data
                activity?.runOnUiThread {
                    // Format the response and update Spinner
                    val breedNamesArray = extractBreedNames(responseData)

                    // Get the spinner
                    val spinnerKindOfPet: Spinner? = view?.findViewById(R.id.spinner_kind_of_a_pet)

                    // Create an ArrayAdapter using the retrieved breed names and a default spinner layout
                    val adapter = ArrayAdapter<String>(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        breedNamesArray
                    )

                    // Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    // Apply the adapter to the spinner
                    spinnerKindOfPet?.adapter = adapter

                    // Set the selection based on the post's kind
                    val index = breedNamesArray.indexOf(post.kind)
                    if (index != -1) {
                        spinnerKindOfPet?.setSelection(index)
                    }
                }
            }
        })
    }

    private fun extractBreedNames(responseData: String?): Array<String> {
        if (responseData.isNullOrEmpty()) return emptyArray()

        val jsonArray = JSONArray(responseData)
        val breedNamesList = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val breedName = jsonObject.getString("breedName")
            breedNamesList.add(breedName)
        }

        return breedNamesList.toTypedArray()
    }
}