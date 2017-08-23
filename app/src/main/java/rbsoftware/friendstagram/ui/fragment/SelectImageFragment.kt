package rbsoftware.friendstagram.ui.fragment

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.FileProvider
import android.support.v4.content.Loader
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import permissions.dispatcher.*
import rbsoftware.friendstagram.ImageHandler
import rbsoftware.friendstagram.ImageSelectListener
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.ui.adapter.PicturesAdapter

/**
 * Created by Rushil on 8/18/2017.
 */
@RuntimePermissions()
class SelectImageFragment : Fragment(), LoaderManager.LoaderCallbacks<Cursor> {
    private var adapter: PicturesAdapter? = null
    private var recyclerView: RecyclerView? = null
    private lateinit var onImageSelected: ImageSelectListener

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_select_image, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view?.findViewById(R.id.rv)
        val fab: FloatingActionButton? = view?.findViewById(R.id.fab)

        recyclerView?.layoutManager = GridLayoutManager(context, 3)
        adapter?.let { recyclerView?.adapter = it }

        fab?.setOnClickListener { SelectImageFragmentPermissionsDispatcher.takePictureWithCheck(this) }
        SelectImageFragmentPermissionsDispatcher.loadImagesWithCheck(this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor>? {
        Log.d(TAG, "Loader created")
        return CursorLoader(
                context,
                if (id == EXTERNAL_STORAGE_ID)
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                else
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                arrayOf(
                        MediaStore.Images.ImageColumns._ID,
                        MediaStore.Images.ImageColumns.DATA
                ),
                null,
                null,
                MediaStore.Images.ImageColumns._ID + " DESC"
        )
    }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        Log.d(TAG, "Finished loading")
        if (adapter == null) {
            adapter = data?.let { PicturesAdapter(it, context) }
            adapter?.setOnImageSelectListener(onImageSelected)
            recyclerView?.adapter = adapter
        } else {
            adapter?.changeCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>?) {
        adapter?.changeCursor(null)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // NOTE: delegate the permission handling to generated method
        SelectImageFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults)
    }

    fun setOnImageSelectListener(imageSelectListener: ImageSelectListener) {
        this.onImageSelected = imageSelectListener
        adapter?.setOnImageSelectListener(onImageSelected)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun loadImages() {
        loaderManager.initLoader(EXTERNAL_STORAGE_ID, null, this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun showRationaleForLoadingImages(request: PermissionRequest) {
        AlertDialog.Builder(context)
                .setMessage(R.string.permission_read_storage_rationale)
                .setPositiveButton(R.string.btn_allow) { _, _ -> request.proceed() }
                .setNegativeButton(R.string.btn_deny) { _, _ -> request.cancel() }
                .show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnPermissionDenied(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun showDeniedForLoadingImages() {
        Toast.makeText(context, R.string.permission_read_storage_denied, Toast.LENGTH_SHORT).show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            val photo = ImageHandler.createImageFile()

            Log.d(TAG, "takePicture: " + photo.toString())
            val photoURI = FileProvider.getUriForFile(context, "rbsoftware.friendstagram.fileprovider", photo)
            Log.d(TAG, "takePicture: " + photoURI)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            ImageHandler.addToMediaScanner(photoURI, context)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showRationaleForSavingImages(request: PermissionRequest) {
        AlertDialog.Builder(context)
                .setMessage(R.string.permission_write_storage_rationale)
                .setPositiveButton(R.string.btn_allow) { _, _ -> request.proceed() }
                .setNegativeButton(R.string.btn_deny) { _, _ -> request.cancel() }
                .show()
    }

    @TargetApi(Build.VERSION_CODES.M)
    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    internal fun showDeniedForSavingImages() {
        Toast.makeText(context, R.string.permission_write_storage_denied, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val EXTERNAL_STORAGE_ID = 0
        private const val INTERNAL_STORAGE_ID = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
        private const val TAG = "SelectImageFragment"

        fun newInstance(): SelectImageFragment {
            return SelectImageFragment()
        }
    }
}