package rbsoftware.friendstagram;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.facebook.common.logging.FLog;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements ProfileFragment.ToolbarUpdateListener {

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
                }
            }
        });
    }

    private void showHomeFragment() {
        switchToolbar(false);
        showFragment(new HomeFragment());
    }

    private void showAccountFragment() {
        switchToolbar(true);
        showFragment(new ProfileFragment());
    }

    private void showFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, fragment);
        transaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void switchToolbar(boolean isScrollingFragment) {
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
    }

    @Override
    public void update(int posts, int followers, int following) {
        TextView numPosts = (TextView) findViewById(R.id.num_posts);
        TextView numFollowers = (TextView) findViewById(R.id.num_followers);
        TextView numFollowing = (TextView) findViewById(R.id.num_following);

        SpannableString postsString = new SpannableString(posts + "\nPOSTS");
        SpannableString followersString = new SpannableString(followers + "\nFOLLOWERS");
        SpannableString followingString = new SpannableString(following + "\nFOLLOWING");
        RelativeSizeSpan largerText = new RelativeSizeSpan(2f);
        StyleSpan boldText = new StyleSpan(Typeface.BOLD);
        int postsLength = Integer.toString(posts).length();
        int followersLength = Integer.toString(followers).length();
        int followingLength = Integer.toString(following).length();

        postsString.setSpan(largerText, 0, postsLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        postsString.setSpan(boldText, 0, postsString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        followersString.setSpan(largerText, 0, followersLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        followersString.setSpan(boldText, 0, followersString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        followingString.setSpan(largerText, 0, followingLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        followingString.setSpan(boldText, 0, followingString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        numPosts.setText(postsString);
        numFollowers.setText(followersString);
        numFollowing.setText(followingString);
    }
}
