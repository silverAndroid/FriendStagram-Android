package rbsoftware.friendstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import rbsoftware.friendstagram.model.Post;

public class MainActivity extends AppCompatActivity implements ProfileFragment.UpdateListener, ToolbarManipulator {

    private static final String KEY_CURRENT_TAB = "current_tab";
    private static final String TAG = "MainActivity";

    private int currentTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab_home:
                        showHomeFragment();
                        break;
                    case R.id.tab_camera:
                        showCameraActivity();
                        break;
                    case R.id.tab_account:
                        showAccountFragment();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFragment(currentTab);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_CURRENT_TAB, currentTab);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentTab = savedInstanceState.getInt(KEY_CURRENT_TAB, 0);
        Log.d(TAG, "onRestoreInstanceState: currentTab = " + currentTab);
    }

    private void showHomeFragment() {
        currentTab = 0;
        showFragment(HomeFragment.newInstance(this), false);
    }

    private void showCameraActivity() {
        currentTab = 1;
        Intent intent = new Intent(MainActivity.this, PicturesActivity.class);
        startActivity(intent);
    }

    private void showAccountFragment() {
        currentTab = 2;
        showFragment(ProfileFragment.newInstance(this), false);
    }

    private void showPostFragment(Post post) {
        showFragment(PostFragment.newInstance(this, post), true);
    }

    private void showFragment(int currentTab) {
        switch (currentTab) {
            case 0:
                showHomeFragment();
                break;
            case 1:
                showCameraActivity();
                break;
            case 2:
                showAccountFragment();
                break;
        }
    }

    private void showFragment(Fragment fragment, boolean addToStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, fragment);
        if (addToStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onImageClick(Post post) {
        showPostFragment(post);
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
