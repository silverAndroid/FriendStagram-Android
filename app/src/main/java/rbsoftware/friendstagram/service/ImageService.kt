package rbsoftware.friendstagram.service

import com.cloudinary.Cloudinary
import io.reactivex.Single
import rbsoftware.friendstagram.Constants
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

/**
 * Created by rushil.perera on 2017-01-15.
 */
class ImageService @Inject constructor() {
    private val cloudinary: Cloudinary

    init {
        val config: Map<String, String> = mapOf(
                "cloud_name" to Constants.Cloudinary.CLOUD_NAME,
                "api_key" to Constants.Cloudinary.API_KEY,
                "api_secret" to Constants.Cloudinary.API_SECRET
        )
        cloudinary = Cloudinary(config)
    }

    fun uploadImage(uploadFileStream: InputStream, username: String): Single<Map<*, *>> {
        return Single.create({
            try {
                it.onSuccess(
                        cloudinary.uploader().upload(
                                uploadFileStream,
                                mapOf(
                                        "folder" to username,
                                        "resource_type" to "image"
                                )
                        )
                )
            } catch (e: IOException) {
                it.onError(e)
            }
        })
    }

    companion object {
        private val TAG = "ImageService"
    }
}
