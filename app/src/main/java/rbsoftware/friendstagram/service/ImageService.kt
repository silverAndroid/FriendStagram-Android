package rbsoftware.friendstagram.service

import android.os.Looper
import android.util.Log
import com.cloudinary.Cloudinary
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import io.reactivex.Completable
import io.reactivex.Single
import rbsoftware.friendstagram.Constants
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
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
        private val handler = Looper.getMainLooper()

        fun prefetchImage(imageRequest: ImageRequest, prefetchToDisk: Boolean = true): Completable {
            return Completable.create {
                val dataSource = if (prefetchToDisk) {
                    Fresco.getImagePipeline().prefetchToDiskCache(
                            imageRequest,
                            null
                    )
                } else {
                    Fresco.getImagePipeline().prefetchToBitmapCache(
                            imageRequest,
                            null
                    )
                }
                if (!dataSource.isFinished) {
                    dataSource.subscribe(object : DataSubscriber<Void> {
                        override fun onNewResult(dataSource: DataSource<Void>) {
                            it.onComplete()
                        }

                        override fun onCancellation(dataSource: DataSource<Void>) {
                            it.onComplete()
                        }

                        override fun onProgressUpdate(dataSource: DataSource<Void>) {
                            Log.v(TAG, "progress: ${dataSource.progress}")
                        }

                        override fun onFailure(dataSource: DataSource<Void>) {
                            it.onError(Throwable(dataSource.failureCause))
                        }
                    }, ThreadPoolExecutor(
                            1,
                            Runtime.getRuntime().availableProcessors(),
                            120,
                            TimeUnit.SECONDS,
                            ArrayBlockingQueue<Runnable>(1)
                    ))
                } else {
                    it.onComplete()
                }
            }
        }
    }
}
