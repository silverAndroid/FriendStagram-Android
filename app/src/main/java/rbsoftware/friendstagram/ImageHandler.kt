package rbsoftware.friendstagram

/**
 * Created by silver_android on 1/3/2017.
 */

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Used by the camera Intent
 */
object ImageHandler {
    private val TAG = "ImageHandler"

    /**
     * @return where the file should go which the [android.content.Intent] will handle
     */
    fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMG_$timestamp.jpg"
        val storageDir = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/Friendstagram"
        val image = File(storageDir, imageFileName)

        Log.d(TAG, "createImageFile: parent directory: $storageDir")
        val createdParents = image.parentFile.mkdirs()
        Log.d(TAG, "createImageFile: createdParents: $createdParents")
        return image
    }

    fun addToMediaScanner(contentURI: Uri, context: Context) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        mediaScanIntent.data = contentURI
        context.sendBroadcast(mediaScanIntent)
    }
}
