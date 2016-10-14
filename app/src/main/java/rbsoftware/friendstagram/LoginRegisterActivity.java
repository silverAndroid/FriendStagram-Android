package rbsoftware.friendstagram;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

/**
 * A login screen that offers login via username/password.
 */
public class LoginRegisterActivity extends AppCompatActivity implements LoginFragment.LoginListener, RegisterFragment
        .RegisterListener, FragmentManager.OnBackStackChangedListener {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private View mProgressView;
    private View mFormView;
    private RegisterFragment registerFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true)
                .setRequestListeners(requestListeners)
                .build();
        Fresco.initialize(this, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);

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

        TextView appName = (TextView) findViewById(R.id.app_name);
        Typeface font = Typeface.createFromAsset(getAssets(), "billabong.ttf");
        appName.setTypeface(font);

        loadLoginPage();
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
        mAuthTask = new LoginRegisterActivity.UserLoginTask(username, password);
        mAuthTask.execute((Void) null);
    }

    @Override
    public void register(String name, String email, String username, String password) {
        showProgress(true);
    }

    @Override
    public void loadRegisterPage() {
        RegisterFragment fragment = registerFragment == null ? new RegisterFragment() : registerFragment;
        showFragment(fragment, true, true);
        registerFragment = fragment;

        SimpleDraweeView background = (SimpleDraweeView) findViewById(R.id.background);
        String backgroundURI = "res:/" + R.drawable.register_bg;
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

    private void loadLoginBackground() {
        SimpleDraweeView background = (SimpleDraweeView) findViewById(R.id.background);
        String backgroundURI = "res:/" + R.drawable.login_bg;
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

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

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
            LoginFragment fragment = (LoginFragment) getSupportFragmentManager().findFragmentById(R.id
                    .fragment_container);
            showProgress(false);

            if (success) {
                Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                fragment.onWrongPassword();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
}

