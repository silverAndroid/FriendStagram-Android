package rbsoftware.friendstagram.service

import io.reactivex.Observable
import io.reactivex.Single
import rbsoftware.friendstagram.model.NetResponse
import rbsoftware.friendstagram.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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
        fun register(@Body registerJSON: Map<String, String>): Single<Response<NetResponse<User>>>

        @POST("/users/login")
        fun login(@Body loginJSON: Map<String, String>): Single<Response<NetResponse<String>>>

        @GET("/users/{username}")
        fun getUser(@Path("username") username: String): Single<Response<NetResponse<User>>>

        @POST("/users/logoff")
        fun logout(): Single<User>
    }
}
