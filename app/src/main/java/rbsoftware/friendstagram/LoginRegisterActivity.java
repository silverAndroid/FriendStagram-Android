package rbsoftware.friendstagram;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.HashMap;
import java.util.Map;

import rbsoftware.friendstagram.model.Error;
import rbsoftware.friendstagram.model.Response;
import rbsoftware.friendstagram.model.User;
import rbsoftware.friendstagram.service.AuthenticationService;
import rbsoftware.friendstagram.service.NetworkService;
import rbsoftware.friendstagram.service.UsersService;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A login screen that offers login via username/password.
 */
public class LoginRegisterActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment.RegisterListener, FragmentManager.OnBackStackChangedListener {

    private static final String TAG = "LoginRegisterActivity";
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private Call<Response<String>> mAuthTask = null;
    private Call<Response<User>> mRegisterTask = null;
    private View mProgressView;
    private View mFormView;
    private RegisterFragment registerFragment = null;
    private UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login_register);

        mFormView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.login_progress);

        loadLoginPage();
        usersService = new UsersService();
    }

    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() == 0) {
            loadLoginBackground();
        }
    }

    @Override
    public void login(String username, String password) {
        showProgress(true);

        Map<String, String> loginDetails = new HashMap<>(2, 2);
        loginDetails.put("username", username);
        loginDetails.put("password", password);

        mAuthTask = usersService.getAPI().login(loginDetails);
        mAuthTask.enqueue(new Callback<Response<String>>() {
            @Override
            public void onResponse(Call<Response<String>> call, retrofit2.Response<Response<String>> response) {
                if (response.isSuccessful()) {
                    String token = response.body().getData();
                    AuthenticationService.getInstance().saveToken(token);
                    onLoginSuccess();
                    Toast.makeText(getApplicationContext(), getString(R.string.success_login), Toast.LENGTH_SHORT).show();
                } else {
                    Error error = NetworkService.parseError(response);
                    handleError(error);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<Response<String>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to login", t);
                showProgress(false);
                Toast.makeText(getBaseContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void register(String name, String email, String username, String password) {
        Map<String, String> registerDetails = new HashMap<>(4, 4);
        registerDetails.put("username", username);
        registerDetails.put("password", password);
        registerDetails.put("name", name);
        registerDetails.put("email", email);

        mRegisterTask = usersService.getAPI().register(registerDetails);
        mRegisterTask.enqueue(new Callback<Response<User>>() {
            @Override
            public void onResponse(Call<Response<User>> call, retrofit2.Response<Response<User>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.success_register), Toast.LENGTH_SHORT).show();
                    loadLoginPage();
                } else {
                    Error error = NetworkService.parseError(response);
                    handleError(error);
                }
                showProgress(false);
            }

            @Override
            public void onFailure(Call<Response<User>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to register", t);
                showProgress(false);
                Toast.makeText(getBaseContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void loadRegisterPage() {
        RegisterFragment fragment = registerFragment == null ? new RegisterFragment() : registerFragment;
        showFragment(fragment, true, true);
        registerFragment = fragment;

        SimpleDraweeView background = (SimpleDraweeView) findViewById(R.id.background);
        String backgroundURI = "res:/" + R.drawable.bg_register;
        background.setImageURI(Uri.parse(backgroundURI));
    }

    @Override
    public void loadLoginPage() {
        final FragmentManager manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);
        if (manager.getBackStackEntryCount() == 0) {
            LoginFragment fragment = new LoginFragment();
            showFragment(fragment, false, false);
        } else {
            manager.popBackStack();
        }

        loadLoginBackground();
    }

    private void handleError(Error error) {
        showProgress(false);
        if (error.getMessage().equals("Username is Null") || error.getMessage().equals("Password is Null")) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (fragment instanceof LoginFragment) {
                LoginFragment loginFragment = (LoginFragment) fragment;
                loginFragment.onResponseError(error);
            } else if (fragment instanceof RegisterFragment) {
                RegisterFragment registerFragment = (RegisterFragment) fragment;
                registerFragment.onResponseError(error);
            }
        } else {
            Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadLoginBackground() {
        SimpleDraweeView background = (SimpleDraweeView) findViewById(R.id.background);
        String backgroundURI = "res:/" + R.drawable.bg_login;
        background.setImageURI(Uri.parse(backgroundURI));
    }

    private void showFragment(Fragment fragment, boolean replace, boolean addToStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (addToStack) {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
        } else {
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        }

        if (replace) {
            transaction.replace(R.id.fragment_container, fragment);
        } else {
            transaction.add(R.id.fragment_container, fragment);
        }

        if (addToStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void onLoginSuccess() {
        Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /*public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                fragment.onWrongPassword();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }*/
}

