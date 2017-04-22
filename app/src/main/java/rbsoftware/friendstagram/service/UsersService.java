package rbsoftware.friendstagram.service;

import java.util.Map;

import rbsoftware.friendstagram.model.Response;
import rbsoftware.friendstagram.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by silver_android on 1/5/2017.
 */

public class UsersService extends NetworkService {

    private final UserAPI api;
    private static UsersService instance;

    private UsersService() {
        api = getRetrofit().create(UserAPI.class);
    }

    public static UsersService getInstance() {
        if (instance == null)
            instance = new UsersService();
        return instance;
    }

    public UserAPI getAPI() {
        return api;
    }

    public interface UserAPI {
        @POST("/users")
        Call<Response<User>> register(@Body Map<String, String> registerJSON);

        @POST("/users/login")
        Call<Response<String>> login(@Body Map<String, String> loginJSON);

        @GET("/users/{username}")
        Call<Response<User>> getUser(@Path("username") String username);

        @POST("/users/logoff")
        Call<User> logout();
    }
}
