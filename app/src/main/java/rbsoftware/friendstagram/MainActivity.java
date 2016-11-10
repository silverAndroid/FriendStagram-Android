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
        showFragment(PicturesFragment.newInstance(), true);
    }

    private void showFragment(Fragment fragment, boolean addToStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, fragment);
        if (addToStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    /*private void switchToolbar(boolean isScrollingFragment) {
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbar.setVisibility(isScrollingFragment ? View.VISIBLE : View.GONE);
        Toolbar toolbarRegular = (Toolbar) findViewById(R.id.toolbar_regular);
        toolbarRegular.setVisibility(isScrollingFragment ? View.GONE : View.VISIBLE);
        if (isScrollingFragment) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            SimpleDraweeView backDrop = (SimpleDraweeView) findViewById(R.id.backdrop);
            backDrop.setImageURI(Uri.parse("http://cdn.pcwallart.com/images/cool-backgrounds-hd-space-wallpaper-2.jpg"));

            SimpleDraweeView profilePicture = (SimpleDraweeView) findViewById(R.id.profile);
            profilePicture.setImageURI(Uri.parse("https://premium.wpmudev.org/forums/?bb_attachments=712464&bbat=47619&inline"));
            update(34, 29, 22);
        } else {
            setSupportActionBar(toolbarRegular);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(!isScrollingFragment);
    }*/

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
