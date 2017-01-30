package rbsoftware.friendstagram;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageSelectListener} interface
 * to handle interaction events.
 * Use the {@link SelectImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@RuntimePermissions
public class SelectImageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, ImageSelectListener {

    private static final int EXTERNAL_STORAGE_ID = 0;
    private static final int INTERNAL_STORAGE_ID = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final String TAG = "SelectImageFragment";

    private RecyclerView recyclerView;
    private PicturesAdapter adapter;

    private ImageSelectListener mListener;

    public SelectImageFragment() {
        // Required empty public constructor
    }

    public static SelectImageFragment newInstance() {
        return new SelectImageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_select_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImageFragmentPermissionsDispatcher.takePictureWithCheck(SelectImageFragment.this);
            }
        });
        SelectImageFragmentPermissionsDispatcher.loadImagesWithCheck(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadImages() {
        getLoaderManager().initLoader(EXTERNAL_STORAGE_ID, null, this);
        // TODO: See if any phones use the internal storage ID for photos
//        getLoaderManager().restartLoader(INTERNAL_STORAGE_ID, null, this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForLoadingImages(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
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
        Toast.makeText(getContext(), R.string.permission_read_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photo = ImageHandler.createImageFile();

            if (photo != null) {
                Log.d(TAG, "takePicture: " + photo.toString());
                Uri photoURI = FileProvider.getUriForFile(getContext(), "rbsoftware.friendstagram.fileprovider", photo);
                Log.d(TAG, "takePicture: " + photoURI);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                ImageHandler.addToMediaScanner(photoURI, getContext());
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showRationaleForSavingImages(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
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
        Toast.makeText(getContext(), R.string.permission_write_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        SelectImageFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ImageSelectListener) {
            mListener = (ImageSelectListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ImageSelectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: Loader ID = " + Integer.toString(id));
        return new CursorLoader(
                getContext(),
                id == EXTERNAL_STORAGE_ID ?
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI :
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                new String[]{
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
            recyclerView.setAdapter(adapter = new PicturesAdapter(data, getContext(), this));
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

    private void updateMediaScanner(Uri photoURI) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoURI);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onImageSelected(Uri url) {
        mListener.onImageSelected(url);
    }
}
