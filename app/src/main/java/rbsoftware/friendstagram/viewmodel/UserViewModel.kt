package rbsoftware.friendstagram.viewmodel

import android.arch.lifecycle.ViewModel
import io.reactivex.Single
import rbsoftware.friendstagram.model.LoginResponse
import rbsoftware.friendstagram.model.ServerResponse
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.service.UsersService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rushil on 8/19/2017.
 */
@Singleton
open class UserViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var usersService: UsersService

    open fun login(username: String, password: String): Single<Response<ServerResponse<LoginResponse>>> {
        return usersService.api.login(mapOf(
                "username" to username,
                "password" to password
        ))
    }

    open fun register(name: String, email: String, username: String, password: String): Single<Response<ServerResponse<User>>> {
        return usersService.api.register(mapOf(
                "name" to name,
                "email" to email,
                "username" to username,
                "password" to password
        ))
    }

    fun getUser(username: String): Single<Response<ServerResponse<User>>> {
        return usersService.api.getUser(username)
    }

    fun updateUser(username: String, data: Map<String, Any>): Single<Response<ServerResponse<String>>> {
        return usersService.api.updateUser(username, data)
    }

    fun updateUserProfilePicture(imageURL: String): Single<Response<ServerResponse<String>>> {
        return usersService.api.updateUserProfilePicture(mapOf("image_url" to imageURL))
    }
}