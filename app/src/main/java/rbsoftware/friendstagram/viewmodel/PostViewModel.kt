package rbsoftware.friendstagram.viewmodel

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.service.ImageService
import rbsoftware.friendstagram.service.PostsService
import retrofit2.Response
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rushil on 8/22/2017.
 */
@Singleton
class PostViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var imageService: ImageService
    @Inject
    lateinit var postsService: PostsService

    fun uploadImage(uploadFileStream: FileInputStream, username: String): Single<Map<*, *>> {
        return imageService.uploadImage(uploadFileStream, username)
    }

    fun createPost(post: Post): Single<Response<ServerResponse<Post>>> {
        return postsService.api.createPost(post)
    }
}