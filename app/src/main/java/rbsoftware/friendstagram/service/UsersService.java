package rbsoftware.friendstagram.service;

import java.util.Map;

import rbsoftware.friendstagram.model.Response;
import rbsoftware.friendstagram.model.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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

        @POST("/login")
        Call<Response<String>> login(@Body Map<String, String> loginJSON);

        @POST("/logOff")
        Call<User> logout();
    }
}
