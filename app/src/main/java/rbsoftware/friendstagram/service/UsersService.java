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

public class UsersService {

    private final UserAPI api;

    public UsersService() {
        this(NetworkService.getBaseURL());
    }

    public UsersService(String apiURL) {
        api = NetworkService.getRetrofit().create(UserAPI.class);
    }

    public UserAPI getAPI() {
        return api;
    }

    public interface UserAPI {
        @POST("/users")
        Call<Response<User>> register(@Body Map<String, String> registerDetails);

        @POST("/login")
        Call<Response<String>> login(@Body Map<String, String> loginDetails);

        @POST("/logOff")
        Call<User> logout();
    }
}
