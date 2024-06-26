import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.model.Model
import com.getpet.Model.Entities.PostEntity
import com.getpet.Model.JoinedModel.JoinedPostModel
import com.getpet.Model.ModelRoom.Dao.PostDao
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val postsModel = JoinedPostModel()

    // Define LiveData to hold the list of posts
    private lateinit var _posts: LiveData<List<PostEntity>>
    val posts: LiveData<List<PostEntity>> get() = _posts

    // Call this function to fetch posts
    fun fetchPosts() {
        _posts = postsModel.getAllPosts()
    }
}



