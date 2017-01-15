package rbsoftware.friendstagram.service;

import java.util.Map;

import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by rushil.perera on 2017-01-15.
 */

public class PostsService {

    private final PostAPI api;

    public PostsService() {
        api = NetworkService.getRetrofit().create(PostAPI.class);
    }

    public PostAPI getAPI() {
        return api;
    }

    public interface PostAPI {
        @POST("/posts")
        Call<Response<Post>> createPost(@Body Map<String, String> postJSON);
    }
}
