package rbsoftware.friendstagram;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import rbsoftware.friendstagram.model.Post;

public class MainActivity extends AppCompatActivity implements ProfileFragment.UpdateListener, ToolbarManipulator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottom_bar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_home) {
                    showHomeFragment();
                } else if (tabId == R.id.tab_account) {
                    showAccountFragment();
                } else if (tabId == R.id.tab_camera) {
                    showPicturesFragment();
                }
            }
        });
    }

    private void showHomeFragment() {
        showFragment(HomeFragment.newInstance(this), false);
    }

    private void showAccountFragment() {
        showFragment(ProfileFragment.newInstance(this), false);
    }

    private void showPicturesFragment() {
        showFragment(PicturesFragment.newInstance(), false);
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
        showPicturesFragment();
    }

    @Override
    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
