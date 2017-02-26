package rbsoftware.friendstagram.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.Response;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by rushil.perera on 2017-01-15.
 */

public class PostsService extends NetworkService {

    private final PostAPI api;

    public PostsService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("token", AuthenticationService.getInstance().getToken());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        retrofitBuilder.client(client);

        api = retrofitBuilder.build().create(PostAPI.class);
    }

    public PostAPI getAPI() {
        return api;
    }

    public interface PostAPI {
        @POST("/posts")
        Call<Response<Post>> createPost(@Body Post postJSON);

        @GET("/posts/{username}")
        Call<Response<ArrayList<Post>>> getPosts(@Path("username") String username);
    }
}
