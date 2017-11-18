package rbsoftware.friendstagram.service

import io.reactivex.Single
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.Post
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Inject

/**
 * Created by Rushil on 8/21/2017.
 */
class PostsService @Inject constructor(retrofit: Retrofit) {
    val api: PostAPI

    init {
        api = retrofit.create(PostAPI::class.java)
    }

    interface PostAPI {
        @POST("/posts")
        fun createPost(@Body postJSON: Post): Single<Response<ServerResponse<Post>>>

        @GET("/posts/{username}")
        fun getPosts(@Path("username") username: String): Call<ServerResponse<List<Post>>>
    }
}