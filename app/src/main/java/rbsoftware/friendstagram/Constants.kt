package rbsoftware.friendstagram

/**
 * Created by rushil.perera on 2017-01-15.
 */

class Constants {
    object Action {
        val EDIT_PROFILE = "edit_profile"
    }

    object Application {
        val BASE_URL = BuildConfig.BASE_URL
    }

    object Cloudinary {
        val API_KEY = BuildConfig.CLOUDINARY_API_KEY
        val API_SECRET = BuildConfig.CLOUDINARY_API_SECRET
        val CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
    }

    object Error {
        val ERROR_FIELD_REQUIRED = "This field is required"
        val ERROR_INVALID_EMAIL = "This email address is invalid"
        val ERROR_INVALID_PASSWORD = "This password is too short"
    }
}
