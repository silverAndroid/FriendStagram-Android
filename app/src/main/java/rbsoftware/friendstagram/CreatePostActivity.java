package rbsoftware.friendstagram;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class CreatePostActivity extends AppCompatActivity implements ImageSelectListener {

    private boolean showNext;
    private Uri imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        showImageSelectFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return showNext;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_next:
                showFilterSelectFragment();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof SelectImageFragment) {
            // Workaround to fix issue 5
            NavUtils.navigateUpFromSameTask(this);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onImageSelected(Uri uri) {
        showNext = true;
        imageURI = uri;
        invalidateOptionsMenu();
    }

    private void showImageSelectFragment() {
        showFragment(SelectImageFragment.newInstance(), false);
    }

    private void showFilterSelectFragment() {
        assert imageURI != null;
        showFragment(SelectFilterFragment.newInstance(imageURI), true);
    }

    private void showFragment(Fragment fragment, boolean addToStack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, fragment);
        if (addToStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }
}
