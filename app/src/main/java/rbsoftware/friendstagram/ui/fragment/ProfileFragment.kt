package rbsoftware.friendstagram.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Action
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.ui.adapter.ProfileAdapter
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.HttpException

/**
 * Created by Rushil on 8/18/2017.
 */
class ProfileFragment : Fragment() {
    private val adapterInitialized: CompletableSubject = CompletableSubject.create()
    private val setToolbar: PublishSubject<Toolbar> = PublishSubject.create()

    private var adapter: ProfileAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var profilePicture: SimpleDraweeView? = null
    private var backgroundPicture: SimpleDraweeView? = null
    private lateinit var username: String
    private lateinit var updateViews: List<UpdateView>
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments.getString(ARG_USERNAME)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(context, 3)
        recyclerView = rv
        profilePicture = profile
        backgroundPicture = background
        updateViews = listOf(
                UpdateView("posts", num_posts, { it.posts.size }),
                UpdateView("followers", num_followers, { it.followerUserIDs.size }),
                UpdateView("following", num_following, { it.followingUserIDs.size })
        )

        toolbar.setBackgroundColor(0)
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapter: ProfileAdapter = recyclerView?.adapter as ProfileAdapter
                return when {
                    adapter.isHeader(position) == true -> layoutManager.spanCount
                    else -> 1
                }
            }
        }
        toolbar?.let { setToolbar.onNext(it) }

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        userViewModel = daggerComponent.userViewModel()

        recyclerView?.layoutManager = layoutManager
        loadProfile()
    }

    fun isAdapterInitialized(): CompletableSubject = adapterInitialized

    fun getToolbarManipulator(): PublishSubject<Toolbar> = setToolbar

    fun getOnPostSelected(): PublishSubject<Post>? = adapter?.getOnPostSelected()

    fun getOnActionExecuted(): PublishSubject<Action>? = adapter?.getOnActionExecuted()

    @SuppressLint("RxLeakedSubscription")
    private fun loadProfile() {
        userViewModel.getUser(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        val user: User? = response.body()?.data
                        user?.let {
                            setAdapter(it)
                            update(it)
                        }
                    } else {
                        onError(HttpException(response), "Failed to load profile")
                    }
                }, { onError(it) })
    }

    private fun setAdapter(user: User) {
        adapter = adapter ?: ProfileAdapter(user.posts, user)

        adapter?.setUser(user)
        adapterInitialized.onComplete()

        recyclerView?.adapter = adapter
    }

    private fun update(user: User) {
        val largerText = RelativeSizeSpan(2f)
        updateViews.forEach {
            val value = it.getValue(user).toString()
            val string = SpannableString("""
                $value
                ${it.name}
                """.trimIndent()
            )

            string.setSpan(largerText, 0, value.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            it.textView?.text = string
        }
        user.profilePictureURL?.let { profilePicture?.setImageURI(it) }
        user.backgroundPictureURL?.let { backgroundPicture?.setImageURI(it) }
    }

    private fun onError(error: Throwable, message: String? = null) {
        Log.e(TAG, message ?: "ErrorResponse", error)
        Toast.makeText(context, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
    }

    private data class UpdateView(var name: String, val textView: TextView?, val getValue: (User) -> Any) {
        init {
            name = name.toUpperCase()
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private const val ARG_USERNAME: String = "username"

        fun newInstance(username: String): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }
}