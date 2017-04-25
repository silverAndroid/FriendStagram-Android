package rbsoftware.friendstagram.ui.activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

import rbsoftware.friendstagram.ImageSelectListener;
import rbsoftware.friendstagram.R;
import rbsoftware.friendstagram.ui.fragment.SelectFilterFragment;
import rbsoftware.friendstagram.ui.fragment.SelectImageFragment;
import rbsoftware.friendstagram.ui.fragment.SharePostFragment;
import rbsoftware.friendstagram.model.Error;
import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.Response;
import rbsoftware.friendstagram.service.AuthenticationService;
import rbsoftware.friendstagram.service.ImageService;
import rbsoftware.friendstagram.service.NetworkService;
import rbsoftware.friendstagram.service.PostsService;
import retrofit2.Call;
import retrofit2.Callback;

public class CreatePostActivity extends AppCompatActivity implements ImageSelectListener {

    private static final String TAG = "CreatePostActivity";
    private Uri imageURI;
    private String caption;

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
            showSharePostFragment();
        } else if (fragment instanceof SharePostFragment) {
            if (isValidForm(fragment)) {
                uploadImage();
            }
        }
    }

    private boolean isValidForm(Fragment fragment) {
        assert fragment instanceof SharePostFragment;
        SharePostFragment postFragment = (SharePostFragment) fragment;
        if (TextUtils.isEmpty(postFragment.getCaption())) {
            postFragment.showRequiredCaption();
            return false;
        }
        caption = postFragment.getCaption();
        return true;
    }

    private void uploadImage() {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Uploading image...");
        try {
            FileInputStream inputStream = new FileInputStream(new File(new URI(imageURI.toString().replace(" ", "%20"))));
            ImageService.getInstance().uploadImage(inputStream, AuthenticationService.getInstance().getUsername(), new ImageService.ImageResponseHandler<Map>() {
                @Override
                public void onComplete(Map response) {
                    if (response.containsKey("exception")) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "nextClicked: Failed to upload image", (IOException) response.get("exception"));
                    } else {
                        Log.d(TAG, "onComplete: Image upload successful!");
                        final String publicURL = (String) response.get("secure_url"); // ID to reference image in Cloudinary
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createPost(publicURL, caption);
                            }
                        });
                    }
                    dialog.dismiss();
                }
            });
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "nextClicked: Failed to upload image", e);
            dialog.dismiss();
        } catch (URISyntaxException e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "uploadImage: Invalid URI", e);
            dialog.dismiss();
        }
    }

    private void createPost(String imageURL, String caption) {
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Sharing post...");
        Call<Response<Post>> createPostTask = PostsService.getInstance().getAPI().createPost(new Post(imageURL, caption, new ArrayList<String>()));
        createPostTask.enqueue(new Callback<Response<Post>>() {
            @Override
            public void onResponse(Call<Response<Post>> call, retrofit2.Response<Response<Post>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Image upload successful");
                    Toast.makeText(getApplicationContext(), getString(R.string.success_image_upload), Toast.LENGTH_SHORT).show();
                    NavUtils.navigateUpFromSameTask(CreatePostActivity.this);
                } else {
                    Error error = NetworkService.parseError(response);
                    //TODO: Handle error
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Response<Post>> call, Throwable t) {
                Log.e(TAG, "onFailure: Failed to create post", t);
                dialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.error_occurred), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showImageSelectFragment() {
        showFragment(SelectImageFragment.newInstance(), false);
    }

    private void showFilterSelectFragment() {
        assert imageURI != null;
        showFragment(SelectFilterFragment.newInstance(imageURI), true);
    }

    private void showSharePostFragment() {
        assert imageURI != null;
        showFragment(SharePostFragment.newInstance(imageURI), true);
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
