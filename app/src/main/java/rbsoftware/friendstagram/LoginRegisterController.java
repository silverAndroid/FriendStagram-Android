package rbsoftware.friendstagram;

import java.util.HashMap;
import java.util.Map;

import rbsoftware.friendstagram.model.Response;
import rbsoftware.friendstagram.model.User;
import rbsoftware.friendstagram.service.UsersService;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by rushil.perera on 2017-01-15.
 */

public class LoginRegisterController {

    private UsersService usersService;

    public LoginRegisterController() {
        usersService = new UsersService();
    }

    public void login(String username, String password, Callback<Response<String>> callback) {
        Map<String, String> loginDetails = new HashMap<>(2, 2);
        loginDetails.put("username", username);
        loginDetails.put("password", password);

        Call<Response<String>> mAuthTask = usersService.getAPI().login(loginDetails);
        mAuthTask.enqueue(callback);
    }

    public void register(String username, String password, String name, String email, Callback<Response<User>> callback) {
        Map<String, String> registerDetails = new HashMap<>(4, 4);
        registerDetails.put("username", username);
        registerDetails.put("password", password);
        registerDetails.put("name", name);
        registerDetails.put("email", email);

        Call<Response<User>> mRegisterTask = usersService.getAPI().register(registerDetails);
        mRegisterTask.enqueue(callback);
    }
}
