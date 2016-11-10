package rbsoftware.friendstagram;

import android.app.Application;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.karumi.dexter.Dexter;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by silver_android on 10/11/16.
 */

public class InitializerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(getBaseContext());

        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }
}
