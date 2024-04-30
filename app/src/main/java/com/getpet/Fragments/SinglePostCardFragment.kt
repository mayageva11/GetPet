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
import okhttp3.*
import java.io.IOException
import org.json.JSONArray

class SinglePostCardFragment : Fragment() {

    private lateinit var post: PostEntity
    private lateinit var image: ImageView
    private lateinit var kindTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var aboutTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var apiTextView: TextView

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
        apiTextView = view.findViewById(R.id.Api)

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

        // Call the API to fetch facts about the specific dog breed
        fetchDogFacts(post.kind)

        // Inflate the layout for this fragment
        return view
    }

    private fun fetchDogFacts(breed: String) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://dogbreeddb.p.rapidapi.com/?search=$breed")
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
                    // Format the response and update TextView
                    apiTextView.text = formatDogFacts(responseData)
                }
            }
        })
    }

    private fun formatDogFacts(responseData: String?): String {
        if (responseData.isNullOrEmpty()) return "No data available"

        val jsonArray = JSONArray(responseData)

        val formattedData = StringBuilder()

        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)

            // Extract relevant fields from the JSON object
            val breedName = jsonObject.getString("breedName")
            val funFact = jsonObject.getString("breedDescription")
            val furColor = jsonObject.getString("furColor")

            // Append formatted data to the string builder
            formattedData.append("Breed Name: $breedName\n")
            formattedData.append("Fun Fact: $funFact\n")
            formattedData.append("Fur Color: $furColor\n\n")
        }

        return formattedData.toString()
    }
}
