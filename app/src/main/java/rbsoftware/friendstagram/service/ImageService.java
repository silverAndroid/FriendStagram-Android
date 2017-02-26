package rbsoftware.friendstagram.service;

import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.base.Function;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import rbsoftware.friendstagram.Constants;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

/**
 * Created by rushil.perera on 2017-01-15.
 */
public class ImageService {
    private static final String TAG = "ImageService";
    private static ImageService ourInstance;
    private Cloudinary cloudinary;

    public static ImageService getInstance() {
        if (ourInstance == null)
            ourInstance = new ImageService();
        return ourInstance;
    }

    private ImageService() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", Constants.Cloudinary.CLOUD_NAME);
        config.put("api_key", Constants.Cloudinary.API_KEY);
        config.put("api_secret", Constants.Cloudinary.API_SECRET);
        cloudinary = new Cloudinary(config);
    }

    public void uploadImage(final InputStream uploadFileStream, final String username, final ImageResponseHandler<Map> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.onComplete(cloudinary.uploader().upload(uploadFileStream, ObjectUtils.asMap("folder", username, "resource_type", "image")));
                } catch (IOException e) {
                    Map<String, IOException> exceptionMap = new HashMap<>();
                    exceptionMap.put("exception", e);
                    callback.onComplete(exceptionMap);
                    Log.e(TAG, "run: Failed to upload image", e);
                }
            }
        }).start();
    }

    public void getImageURI(final String publicID, final ImageResponseHandler<String> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                callback.onComplete(cloudinary.url().signed(true).type("authenticated").generate(publicID));
            }
        }).start();
    }

    public interface ImageResponseHandler<T> {
        void onComplete(T response);
    }

    public interface ImageAPI {
        @Multipart
        @POST("/image/upload")
        Call<Map<String, String>> upload();
    }
}
