package rbsoftware.friendstagram.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rbsoftware.friendstagram.Constants;

/**
 * Created by rushil.perera on 2017-01-15.
 */
public class ImageService {
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

    public Map<String, String> uploadImage(String uploadFilePath, String username) throws IOException {
        return cloudinary.uploader().upload(uploadFilePath, ObjectUtils.asMap("folder", username, "resource_type", "image", "type", "authenticated"));
    }
}
