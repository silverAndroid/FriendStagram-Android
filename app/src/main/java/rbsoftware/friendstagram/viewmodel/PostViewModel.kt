package rbsoftware.friendstagram.viewmodel

import android.arch.lifecycle.ViewModel
import android.net.Uri
import io.reactivex.Single
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.service.ImageService
import rbsoftware.friendstagram.service.PostsService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rushil on 8/22/2017.
 */
@Singleton
class PostViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var postsService: PostsService

    fun uploadImage(imageUri: Uri, username: String): Single<String> {
        return ImageService.uploadImage(imageUri, username)
    }

    fun createPost(post: Post): Single<Response<ServerResponse<Post>>> {
        return postsService.api.createPost(post)
    }
}