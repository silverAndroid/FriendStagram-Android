package rbsoftware.friendstagram

/**
 * Created by rushil.perera on 2017-01-15.
 */

object Actions {
    val EDIT_PROFILE = "edit_profile"
    val FOLLOW_USER =  "follow_user"
    val UNFOLLOW_USER = "unfollow_user"
}

object ApplicationConfig {
    val BASE_URL = BuildConfig.BASE_URL
}

object Cloudinary {
    val API_KEY = BuildConfig.CLOUDINARY_API_KEY
    val API_SECRET = BuildConfig.CLOUDINARY_API_SECRET
    val CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
}

object Errors {
    val ERROR_FIELD_REQUIRED = "This field is required"
    val ERROR_INVALID_EMAIL = "This email address is invalid"
    val ERROR_INVALID_PASSWORD = "This password is too short"
}
