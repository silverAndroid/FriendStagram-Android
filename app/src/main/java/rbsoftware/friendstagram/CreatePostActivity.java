package rbsoftware.friendstagram;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

import rbsoftware.friendstagram.service.AuthenticationService;
import rbsoftware.friendstagram.service.ImageService;

public class CreatePostActivity extends AppCompatActivity implements ImageSelectListener {

    private static final String TAG = "CreatePostActivity";
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
        return imageURI != null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_next:
                nextClicked();
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
        imageURI = uri;
        invalidateOptionsMenu();
    }

    private void nextClicked() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment instanceof SelectImageFragment) {
            showFilterSelectFragment();
        } else if (fragment instanceof SelectFilterFragment) {
            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Uploading image...");
        try {
            FileInputStream inputStream = new FileInputStream(new File(new URI(imageURI.toString())));
            ImageService.getInstance().uploadImage(inputStream, AuthenticationService.getInstance().getUsername(), new ImageService.ImageResponseHandler() {
                @Override
                public void onComplete(Map response) {
                    if (response.containsKey("exception")) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "nextClicked: Failed to upload image", (IOException) response.get("exception"));
                    } else {
                        Log.d(TAG, "onComplete: Image upload successful!");
                        String publicID = (String) response.get("public_id"); // ID to reference image in Cloudinary
                    }
                    dialog.dismiss();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "nextClicked: Failed to upload image", e);
        } catch (URISyntaxException e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "uploadImage: Invalid URI", e);
        }
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
