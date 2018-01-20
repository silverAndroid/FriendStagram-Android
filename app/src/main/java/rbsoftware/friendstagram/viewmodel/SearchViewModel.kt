package rbsoftware.friendstagram.viewmodel

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.service.SearchService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by silve on 1/18/2018.
 */
@Singleton
class SearchViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var searchService: SearchService

    fun searchUsers(query: String): Single<Response<ServerResponse<List<User>>>> = searchService.api.searchUsers(query)
}