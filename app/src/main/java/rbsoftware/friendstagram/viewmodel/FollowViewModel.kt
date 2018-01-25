package rbsoftware.friendstagram.viewmodel

import android.arch.lifecycle.ViewModel
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.service.FollowService
import javax.inject.Inject

/**
 * Created by silve on 1/25/2018.
 */
class FollowViewModel @Inject constructor(): ViewModel() {
    @Inject
    lateinit var followService: FollowService

    fun followUser(user: User) = followService.api.followUser(mapOf("followUsername" to user.username))

    fun unfollowUser(user: User) = followService.api.unfollowUser(mapOf("unfollowUsername" to user.username))
}