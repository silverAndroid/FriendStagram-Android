package rbsoftware.friendstagram.model

import com.google.gson.annotations.SerializedName

/**
 * Created by silver_android on 26/11/17.
 */
class LoginResponse(
        private val token: String = "",
        private val verified: Boolean = false,
        @SerializedName("profile_picture_url") private val profilePictureURL: String? = ""
) {
    operator fun component1(): String = token

    operator fun component2(): Boolean = verified

    operator fun component3(): String? = profilePictureURL
}