package rbsoftware.friendstagram

/**
 * Created by rushil.perera on 2017-01-15.
 */

class Constants {
    object Cloudinary {
        val API_KEY = BuildConfig.CLOUDINARY_API_KEY
        val API_SECRET = BuildConfig.CLOUDINARY_API_SECRET
        val CLOUD_NAME = BuildConfig.CLOUDINARY_CLOUD_NAME
    }

    object Application {
        val BASE_URL = BuildConfig.BASE_URL
    }
}
