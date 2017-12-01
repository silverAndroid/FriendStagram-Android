package rbsoftware.friendstagram.service

import io.reactivex.Single
import rbsoftware.friendstagram.model.LoginResponse
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*
import javax.inject.Inject

/**
 * Created by silver_android on 1/5/2017.
 */

class UsersService @Inject constructor(retrofit: Retrofit) {
    val api: UserAPI

    init {
        api = retrofit.create(UserAPI::class.java)
    }

    interface UserAPI {
        @POST("/users")
        fun register(@Body registerJSON: Map<String, String>): Single<Response<ServerResponse<User>>>

        @POST("/users/login")
        fun login(@Body loginJSON: Map<String, String>): Single<Response<ServerResponse<LoginResponse>>>

        @GET("/users/{username}")
        fun getUser(@Path("username") username: String): Single<Response<ServerResponse<User>>>

        @PUT("/users/{username}")
        fun updateUser(@Path("username") username: String, @Body updateJSON: Map<String, @JvmSuppressWildcards Any>): Single<Response<ServerResponse<String>>>

        @PUT("/users/profile_picture")
        fun updateUserProfilePicture(@Body imageURL: Map<String, String>): Single<Response<ServerResponse<String>>>

        @POST("/users/logoff")
        fun logout(): Single<User>
    }
}
