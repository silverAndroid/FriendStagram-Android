package rbsoftware.friendstagram.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_setup.*
import rbsoftware.friendstagram.BuildConfig
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.inflate
import rbsoftware.friendstagram.ui.activity.SelectImageActivity
import rbsoftware.friendstagram.viewmodel.PostViewModel
import rbsoftware.friendstagram.viewmodel.UserViewModel

/**
 * A simple [Fragment] subclass.
 */
class SetupFragment : Fragment() {
    val setupComplete: CompletableSubject = CompletableSubject.create()
    val showLoading: PublishSubject<Boolean> = PublishSubject.create()

    private lateinit var username: String
    private lateinit var postViewModel: PostViewModel
    private lateinit var userViewModel: UserViewModel
    private lateinit var uploadSingle: Disposable
    private lateinit var imageURI: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments.getString(ARG_USERNAME)
        imageURI = getDefaultProfilePictureURL()

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        postViewModel = daggerComponent.postViewModel()
        userViewModel = daggerComponent.userViewModel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return container?.inflate(R.layout.fragment_setup, false, inflater)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDefaultPicture.visibility = View.GONE

        setDefaultProfilePicture()
        btnDefaultPicture.setOnClickListener {
            setDefaultProfilePicture()
            btnDefaultPicture.visibility = View.GONE
        }
        btnSelectImage.setOnClickListener {
            val intent = Intent(context, SelectImageActivity::class.java)
            startActivityForResult(intent, REQUEST_IMAGE_URI)
        }
        btnNext.setOnClickListener {
            uploadProfilePicture(imageURI, username)
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::uploadSingle.isInitialized && !uploadSingle.isDisposed)
            uploadSingle.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_URI) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    imageURI = it.getParcelableExtra(SelectImageActivity.URI_RESULT_KEY)
                    setProfilePicture(imageURI)
                    btnDefaultPicture.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun uploadProfilePicture(imageUri: Uri, username: String) {
        showLoading.onNext(true)
        uploadSingle = postViewModel.uploadImage(imageUri, username)
                .flatMap { imageURL ->
                    userViewModel.updateUserProfilePicture(imageURL)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.io())
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    showLoading.onNext(false)
                    if (response.isSuccessful) {
                        setupComplete.onComplete()
                        Log.d(TAG, "Uploaded profile successfully")
                        Toast.makeText(context, getString(R.string.setup_success), Toast.LENGTH_SHORT).show()
                    } else {
                        TODO()
                    }
                }, this::onNetworkError)
    }

    private fun setDefaultProfilePicture() {
        setProfilePicture(getDefaultProfilePictureURL())
    }

    private fun setProfilePicture(uri: Uri) {
        profile.setImageURI(uri)
    }

    private fun onNetworkError(err: Throwable) {
        showLoading.onNext(false)
        Toast.makeText(context, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
        Log.e(TAG, "An error occurred", err)
    }

    private fun getDefaultProfilePictureURL(): Uri = Uri.parse("${BuildConfig.BASE_URL}/users/default_profile_picture?username=$username")

    companion object {
        private const val TAG = "SetupFragment"
        private const val REQUEST_IMAGE_URI = 1
        private const val ARG_USERNAME: String = "username"

        fun newInstance(username: String): SetupFragment {
            val fragment = SetupFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }
}
