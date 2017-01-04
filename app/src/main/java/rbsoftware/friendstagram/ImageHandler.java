package rbsoftware.friendstagram;

/**
 * Created by silver_android on 1/3/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Used by the camera Intent
 */
public class ImageHandler {

    private static final String TAG = "ImageHandler";

    /**
     * @return where the file should go which the {@link android.content.Intent} will handle
     */
    public static File createImageFile() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("IMG_%s.jpg", timestamp);
        String storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Friendstagram";
        File image = new File(storageDir, imageFileName);

        Log.d(TAG, "createImageFile: parent directory: " + storageDir);
        boolean createdParents = image.getParentFile().mkdirs();
        Log.d(TAG, "createImageFile: createdParents = " + createdParents);
        return image;
    }

    public static void addToMediaScanner(Uri contentURI, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentURI);
        context.sendBroadcast(mediaScanIntent);
    }
}
