package rbsoftware.friendstagram.ui.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.onError
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.service.ImageService
import rbsoftware.friendstagram.showProgress
import rbsoftware.friendstagram.ui.activity.SelectImageActivity
import rbsoftware.friendstagram.ui.adapter.EditProfileAdapter
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.HttpException

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileFragment : Fragment() {
    private val updateComplete: CompletableSubject = CompletableSubject.create()
    private val subscriptions = CompositeDisposable()

    private var loading: Boolean = false
        set(value) {
            field = value
            showProgress(value, loadingView, layoutView)
        }

    private var imageID = -1
    private lateinit var user: User
    private lateinit var adapter: EditProfileAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments.getParcelable(ARG_USER)
        adapter = EditProfileAdapter(user)
        subscriptions.add(
                adapter.imageClickListener
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.trampoline())
                        .subscribe({
                            imageID = it
                            val intent = Intent(context, SelectImageActivity::class.java)
                            startActivityForResult(intent, SelectImageActivity.REQUEST_URI)
                        }, { onError(TAG, context, it) })
        )
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_edit_profile, container, false)
    }

    @SuppressLint("RxLeakedSubscription")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = rv
        val btnSubmit = submit

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
        userViewModel = daggerComponent.userViewModel()

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
        btnSubmit?.setOnClickListener {
            val adapterData = adapter.getData()
            adapterData?.let { data ->
                loading = true
                val urls = listOf(
                        data["profile_picture_url"] as String,
                        data["profile_background_url"] as String
                )
                val uploadSingles = urls.map {
                    if (it.startsWith("file://")) {
                        ImageService.uploadImage(Uri.parse(it), authService.username)
                    } else {
                        Single.just(it)
                    }
                }

                Single.zip(uploadSingles) {
                    val map = data.toMutableMap()
                    map["profile_picture_url"] = it[0] as String
                    map["profile_background_url"] = it[1] as String
                    map
                }
                        .flatMap { userViewModel.updateUser(authService.username, it) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            loading = false
                            if (response.isSuccessful) {
                                val message = response.body()?.data
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                updateComplete.onComplete()
                            } else {
                                onError(TAG, context, HttpException(response))
                            }
                        }, {
                            loading = false
                            onError(TAG, context, it, "Failed to update user")
                        })
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SelectImageActivity.REQUEST_URI) {
            if (resultCode == Activity.RESULT_OK) {
                data?.let {
                    val imageURI: Uri = it.getParcelableExtra(SelectImageActivity.URI_RESULT_KEY)
                    adapter.updateImage(imageURI, imageID)
                }
            }
        }
    }

    fun updateComplete() = updateComplete

    companion object {
        private const val TAG = "EditProfileFragment"
        private const val ARG_USER = "user"

        fun newInstance(user: User): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}