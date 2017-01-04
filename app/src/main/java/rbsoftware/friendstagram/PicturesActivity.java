package rbsoftware.friendstagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

// TODO: Check if using the LoaderManager that is not in the support library will causes backwards-compatibility issues
@RuntimePermissions
public class PicturesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXTERNAL_STORAGE_ID = 0;
    private static final int INTERNAL_STORAGE_ID = 1;
    private static final String TAG = "PicturesActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private RecyclerView recyclerView;
    private PicturesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PicturesActivityPermissionsDispatcher.takePictureWithCheck(PicturesActivity.this);
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 3));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }

        PicturesActivityPermissionsDispatcher.loadImagesWithCheck(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadImages() {
        getLoaderManager().initLoader(EXTERNAL_STORAGE_ID, null, this);
//        getLoaderManager().restartLoader(INTERNAL_STORAGE_ID, null, PicturesActivity.this); // TODO: See if any phones use the internal storage ID for photos
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForLoadingImages(final PermissionRequest request) {
        new AlertDialog.Builder(getBaseContext())
                .setMessage(R.string.permission_read_storage_rationale)
                .setPositiveButton(R.string.btn_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.btn_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showDeniedForLoadingImages() {
        Toast.makeText(getApplicationContext(), R.string.permission_read_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photo = ImageHandler.createImageFile();

            if (photo != null) {
                Log.d(TAG, "takePicture: " + photo.toString());
                Uri photoURI = FileProvider.getUriForFile(getBaseContext(), "rbsoftware.friendstagram.fileprovider", photo);
                Log.d(TAG, "takePicture: " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                updateMediaScanner(photoURI);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForSavingImages(final PermissionRequest request) {
        new AlertDialog.Builder(getBaseContext())
                .setMessage(R.string.permission_write_storage_rationale)
                .setPositiveButton(R.string.btn_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.btn_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showDeniedForSavingImages() {
        Toast.makeText(getApplicationContext(), R.string.permission_write_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: Loader ID = " + Integer.toString(id));
        return new CursorLoader(
                getBaseContext(),
                id == EXTERNAL_STORAGE_ID ?
                        MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI :
                        MediaStore.Images.Thumbnails.INTERNAL_CONTENT_URI,
                new String[] {
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA
                },
                null,
                null,
                MediaStore.Images.ImageColumns._ID + " DESC"
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: cursor: " + (data == null ? "null" : Integer.toString(data.getCount()) + " elements"));
        if (adapter == null) {
            recyclerView.setAdapter(adapter = new PicturesAdapter(data));
        } else {
            adapter.changeCursor(data);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            Log.i(TAG, "onLoaderReset: Resetting loader");
            adapter.changeCursor(null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        PicturesActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private void updateMediaScanner(Uri photoURI) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoURI);
        sendBroadcast(mediaScanIntent);
    }
}
