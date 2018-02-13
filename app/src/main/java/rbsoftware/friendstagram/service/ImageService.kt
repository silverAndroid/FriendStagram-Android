package rbsoftware.friendstagram.service

import android.net.Uri
import android.os.Looper
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.facebook.datasource.DataSource
import com.facebook.datasource.DataSubscriber
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.request.ImageRequest
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by rushil.perera on 2017-01-15.
 */
object ImageService {
    private const val TAG = "ImageService"

    fun uploadImage(imageUri: Uri, username: String): Single<String> {
        return Single.create {
            try {
                MediaManager
                        .get()
                        .upload(imageUri)
                        .options(mapOf(
                                "folder" to username,
                                "resource_type" to "image"
                        ))
                        .callback(object : UploadCallback {
                            override fun onSuccess(requestId: String, resultData: MutableMap<Any?, Any?>) {
                                it.onSuccess(resultData["secure_url"] as String)
                            }

                            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                                Log.d(TAG, "Uploading image: $bytes/$totalBytes; ID: $requestId")
                            }

                            override fun onReschedule(requestId: String, error: ErrorInfo) {
                                it.onError(Exception("Error ${error.code}: ${error.description}"))
                            }

                            override fun onError(requestId: String, error: ErrorInfo) {
                                it.onError(Exception("Error ${error.code}: ${error.description}"))
                            }

                            override fun onStart(requestId: String) {
                                Log.d(TAG, "Started image upload; ID: $requestId")
                            }
                        })
                        .dispatch()
            } catch (e: Exception) {
                it.onError(e)
            }
        }
    }

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
