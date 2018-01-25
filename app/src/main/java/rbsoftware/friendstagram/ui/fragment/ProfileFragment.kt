package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import rbsoftware.friendstagram.Actions
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Action
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.onError
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.ui.adapter.ProfileAdapter
import rbsoftware.friendstagram.viewmodel.FollowViewModel
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Created by Rushil on 8/18/2017.
 */
class ProfileFragment : Fragment() {
    private val subscriptions: CompositeDisposable = CompositeDisposable()
    private val setToolbar: PublishSubject<Toolbar> = PublishSubject.create()
    private val adapterInitialized: CompletableSubject = CompletableSubject.create()

    private var adapter: ProfileAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var profilePicture: SimpleDraweeView? = null
    private var backgroundPicture: SimpleDraweeView? = null
    private lateinit var username: String
    private lateinit var updateViews: List<UpdateView>
    private lateinit var userViewModel: UserViewModel
    private lateinit var followViewModel: FollowViewModel
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments.getString(ARG_USERNAME)

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        userViewModel = daggerComponent.userViewModel()
        followViewModel = daggerComponent.followViewModel()
        authService = daggerComponent.authService()
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
                UpdateView("followers", num_followers, { it.followerUsers.size }),
                UpdateView("following", num_following, { it.followingUsers.size })
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

        recyclerView?.layoutManager = layoutManager
        loadProfile()
    }

    fun isAdapterInitialized(): CompletableSubject = adapterInitialized

    fun getToolbarManipulator(): PublishSubject<Toolbar> = setToolbar

    fun getOnPostSelected(): PublishSubject<Post>? = adapter?.getOnPostSelected()

    fun getOnActionExecuted(): PublishSubject<Action>? = adapter?.getOnActionExecuted()

    private fun loadProfile() {
        subscriptions.add(
                userViewModel.getUser(username)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                val user: User? = response.body()?.data
                                user?.let {
                                    setAdapter(it)
                                }
                            } else {
                                onError(TAG, context, HttpException(response), "Failed to load profile")
                            }
                        }, { onError(TAG, context, it) })
        )
    }

    private fun setAdapter(user: User) {
        val isMe = authService.username == username
        val isFollowing = user.followerUsers.any { it.username == authService.username }

        if (adapter == null) {
            adapter = ProfileAdapter(user.posts, user, isMe, isFollowing)
            adapterInitialized.onComplete()
            adapter?.getOnActionExecuted()?.let { subject ->
                subscriptions.add(
                        subject.observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.trampoline())
                                .subscribe({
                                    when (it.action) {
                                        Actions.FOLLOW_USER -> followUser(user)
                                        Actions.UNFOLLOW_USER -> unfollowUser(user)
                                    }
                                }, { onError(TAG, context, it) })
                )
            }
        }

        adapter?.setUser(user, isMe, isFollowing)
        recyclerView?.adapter = adapter
        update(user)
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

    private fun followUser(user: User) {
        subscriptions.add(
                followViewModel.followUser(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                user.followerUsers.add(User(authService.username, ""))
                                setAdapter(user)
                            } else {
                                onError(TAG, context, HttpException(response), "Failed to follow user")
                            }
                        }, { onError(TAG, context, it) })
        )
    }

    private fun unfollowUser(user: User) {
        subscriptions.add(
                followViewModel.unfollowUser(user)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                for (i in 0 until user.followerUsers.size) {
                                    val followingUser = user.followerUsers[i]
                                    if (followingUser.username == authService.username) {
                                        user.followerUsers.removeAt(i)
                                        break
                                    }
                                }
                                setAdapter(user)
                            } else {
                                onError(TAG, context, HttpException(response), "Failed to unfollow user")
                            }
                        }, { onError(TAG, context, it) })
        )
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