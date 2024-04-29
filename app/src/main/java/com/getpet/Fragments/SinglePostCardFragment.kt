import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.getpet.Model.Entities.PostEntity
import com.getpet.R

class SinglePostCardFragment : Fragment() {


    private lateinit var post: PostEntity
    private lateinit var image: ImageView
    private lateinit var kindTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var aboutTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var ownerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.single_post_card, container, false)
        // Get the PostEntity object from the arguments
        post = arguments?.getSerializable("post") as PostEntity

        // Find the views in the layout
        image = view.findViewById(R.id.post_image)
        kindTextView = view.findViewById(R.id.post_kind)
        ageTextView = view.findViewById(R.id.post_age)
        aboutTextView = view.findViewById(R.id.post_about)
        locationTextView = view.findViewById(R.id.post_location)
        phoneNumberTextView = view.findViewById(R.id.post_phone)
        ownerTextView = view.findViewById(R.id.post_owner)

        // Set the values to the views
        Glide.with(requireContext())
            .load(post.img)
            .into(image)

        kindTextView.text = post.kind
        ageTextView.text = post.age
        aboutTextView.text = post.about
        locationTextView.text = post.location
        phoneNumberTextView.text = post.phone
        ownerTextView.text = post.owner
        // Inflate the layout for this fragment
        //

        return view
    }



//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize views
//
//
//        // Retrieve the PostEntity object from arguments
//        arguments?.let { args ->
//            post = args.getSerializable("post") as? PostEntity ?: return@let
//        } ?: return
//
//        // Populate views with post data
//
//    }

}


