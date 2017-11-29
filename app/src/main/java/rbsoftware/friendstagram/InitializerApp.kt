package rbsoftware.friendstagram

import android.app.Application
import com.cloudinary.android.MediaManager
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.listener.RequestLoggingListener
import rbsoftware.friendstagram.dagger.component.DaggerServicesComponent
import rbsoftware.friendstagram.dagger.component.ServicesComponent
import rbsoftware.friendstagram.dagger.module.AppModule

/**
 * Created by silver_android on 10/11/16.
 */

class InitializerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val requestListeners = setOf<RequestListener>(RequestLoggingListener())
        val frescoConfig = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setRequestListeners(requestListeners)
                .build()
        val cloudinaryConfig: Map<String, String> = mapOf(
                "cloud_name" to Cloudinary.CLOUD_NAME,
                "api_key" to Cloudinary.API_KEY,
                "api_secret" to Cloudinary.API_SECRET
        )
        servicesComponent = DaggerServicesComponent
                .builder()
                .appModule(AppModule(this))
                .build()

        Fresco.initialize(this, frescoConfig)
        FLog.setMinimumLoggingLevel(FLog.VERBOSE)
        MediaManager.init(this, cloudinaryConfig)
    }

    companion object {
        lateinit var servicesComponent: ServicesComponent
    }
}
