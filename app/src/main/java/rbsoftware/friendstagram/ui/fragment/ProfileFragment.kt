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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import rbsoftware.friendstagram.PostSelectListener
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.ToolbarManipulator
import rbsoftware.friendstagram.dagger.component.DaggerServicesComponent
import rbsoftware.friendstagram.dagger.module.AppModule
import rbsoftware.friendstagram.dagger.module.ServicesModule
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.ui.adapter.ProfileAdapter
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.HttpException

/**
 * Created by Rushil on 8/18/2017.
 */
class ProfileFragment : Fragment() {

    private var toolbar: Toolbar? = null
    private var recyclerView: RecyclerView? = null
    private var profilePicture: SimpleDraweeView? = null
    private var backgroundPicture: SimpleDraweeView? = null
    private lateinit var username: String
    private lateinit var setToolbar: ToolbarManipulator
    private lateinit var onPostSelected: PostSelectListener
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
        toolbar = view?.findViewById(R.id.toolbar)
        recyclerView = view?.findViewById(R.id.rv)
        profilePicture = view?.findViewById(R.id.profile)
        backgroundPicture = view?.findViewById(R.id.backdrop)
        updateViews = listOf(
                UpdateView("posts", view?.findViewById(R.id.num_posts), { user -> user.posts.size }),
                UpdateView("followers", view?.findViewById(R.id.num_followers), { user -> user.followerUserIDs.size }),
                UpdateView("following", view?.findViewById(R.id.num_following), { user -> user.followingUserIDs.size })
        )

        toolbar?.setBackgroundColor(0)
        toolbar?.let { setToolbar(it) }
        layoutManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val adapter: ProfileAdapter = recyclerView?.adapter as ProfileAdapter
                return when {
                    adapter.isHeader(position) == true -> layoutManager.spanCount
                    else -> 1
                }
            }
        }

        val daggerComponent = DaggerServicesComponent
                .builder()
                .appModule(AppModule(context))
                .servicesModule(ServicesModule())
                .build()
        daggerComponent.inject(this)
        userViewModel = daggerComponent.userViewModel()

        recyclerView?.layoutManager = layoutManager
        loadProfile()
    }

    fun setToolbarManipulator(toolbarManipulator: ToolbarManipulator) {
        this.setToolbar = toolbarManipulator
        toolbar?.let { setToolbar(it) }
    }

    fun setOnPostSelectListener(postSelectListener: PostSelectListener) {
        this.onPostSelected = postSelectListener
    }

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
        val adapter = recyclerView?.adapter

        if (adapter == null) {
            val profileAdapter = ProfileAdapter(user.posts, user)
            profileAdapter.setOnPostSelectListener(onPostSelected)
            recyclerView?.adapter = profileAdapter
        } else {
            val profileAdapter = adapter as ProfileAdapter
            profileAdapter.setOnPostSelectListener(onPostSelected)
            profileAdapter.setUser(user)
        }
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
        Log.e(TAG, message ?: "Error", error)
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