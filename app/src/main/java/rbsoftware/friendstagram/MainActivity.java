package rbsoftware.friendstagram;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import rbsoftware.friendstagram.model.Post;

public class MainActivity extends AppCompatActivity implements ProfileFragment.UpdateListener, ToolbarManipulator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_nav);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        showHomeFragment();
                        return true;
                    case R.id.tab_account:
                        showAccountFragment();
                        return true;
                    case R.id.tab_camera:
                        showCameraFragment();
                        return true;
                }
                return false;
            }
        });
        showHomeFragment();
    }

    private void showHomeFragment() {
        showFragment(HomeFragment.newInstance(this), false);
    }

    private void showAccountFragment() {
        showFragment(ProfileFragment.newInstance(this), false);
    }

    private void showCameraFragment() {
        showFragment(PicturesFragment.newInstance(this), false);
    }

    private void showPostFragment(Post post) {
        showFragment(PostFragment.newInstance(this, post), true);
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
