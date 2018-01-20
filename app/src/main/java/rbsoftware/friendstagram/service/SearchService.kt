package rbsoftware.friendstagram.service

import io.reactivex.Single
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

/**
 * Created by silve on 1/18/2018.
 */
class SearchService @Inject constructor(retrofit: Retrofit) {
    val api: SearchAPI = retrofit.create(SearchAPI::class.java)

    interface SearchAPI {
        @GET("/search/users")
        fun searchUsers(@Query("query") query: String): Single<Response<ServerResponse<List<User>>>>
    }
}