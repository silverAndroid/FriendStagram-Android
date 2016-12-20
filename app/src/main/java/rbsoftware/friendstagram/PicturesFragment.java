package rbsoftware.friendstagram;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * A simple {@link Fragment} subclass.
 */
@RuntimePermissions
public class PicturesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXTERNAL_STORAGE_ID = 0;
    private static final int INTERNAL_STORAGE_ID = 1;
    private static final String TAG = "PicturesFragment";
    private static ToolbarManipulator toolbarManipulator;

    private RecyclerView recyclerView;
    private PicturesAdapter adapter;

    public PicturesFragment() {
        // Required empty public constructor
    }

    public static PicturesFragment newInstance(ToolbarManipulator manipulator) {
        PicturesFragment.toolbarManipulator = manipulator;
        return new PicturesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pictures, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarBlack, null));
        } else {
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorToolbarBlack));
        }

        ImageView leftIcon = (ImageView) toolbar.findViewById(R.id.left_icon);
        ImageView rightIcon = (ImageView) toolbar.findViewById(R.id.right_icon);
        leftIcon.setImageResource(R.drawable.ic_close);
        leftIcon.setContentDescription("Back");
        rightIcon.setImageResource(R.drawable.ic_camera);
        rightIcon.setContentDescription("Open Camera");
        toolbarManipulator.setToolbar(toolbar);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        if (adapter != null) {
            recyclerView.setAdapter(adapter);
        }

        PicturesFragmentPermissionsDispatcher.loadImagesWithCheck(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    void loadImages() {
        getLoaderManager().initLoader(EXTERNAL_STORAGE_ID, null, PicturesFragment.this);
        getLoaderManager().restartLoader(INTERNAL_STORAGE_ID, null, PicturesFragment.this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    void showRationaleForStorage(final PermissionRequest request) {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.permission_storage_rationale)
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
    void showDeniedForStorage() {
        Toast.makeText(getContext(), R.string.permission_storage_denied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: Loader ID = " + Integer.toString(id));
        return new CursorLoader(
                getContext(),
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
        PicturesFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
