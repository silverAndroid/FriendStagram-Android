package rbsoftware.friendstagram.ui.adapter

import android.net.Uri
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.subjects.PublishSubject
import rbsoftware.friendstagram.Actions
import rbsoftware.friendstagram.GenericDiffCallback
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.inflate
import rbsoftware.friendstagram.model.Action
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.ui.viewholder.PictureViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class ProfileAdapter(private var posts: List<Post>, private var user: User, private var isMe: Boolean, private var isFollowing: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val onPostSelected: PublishSubject<Post> = PublishSubject.create()
    private val onActionExecuted: PublishSubject<Action> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val view = parent?.inflate(R.layout.header_profile)
            HeaderViewHolder(view, isMe, isFollowing)
        } else {
            val view = parent?.inflate(R.layout.item_image)
            PictureViewHolder(view)
        }
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder?, position: Int) {
        if (isHeader(position)) {
            val holder = parent as HeaderViewHolder
            with(user) {
                holder.name?.text = name
                holder.username?.text = username
                holder.description?.text = description
            }
        } else {
            val holder = parent as PictureViewHolder
            val post = posts[position - 1]
            holder.image?.setImageURI(Uri.parse(post.imageURL))
            holder.image?.setOnClickListener { onPostSelected.onNext(post) }
        }
    }

    override fun getItemCount(): Int {
        return posts.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) {
            ITEM_VIEW_TYPE_HEADER
        } else {
            ITEM_VIEW_TYPE_ITEM
        }
    }

    fun setUser(user: User, isMe: Boolean, isFollowing: Boolean) {
        this.user = user
        this.isMe = isMe
        this.isFollowing = isFollowing
        notifyItemChanged(0)
        setPosts(user.posts)
    }

    fun isHeader(position: Int): Boolean {
        return position == 0
    }

    fun getOnPostSelected(): PublishSubject<Post> = onPostSelected

    fun getOnActionExecuted(): PublishSubject<Action> = onActionExecuted

    private fun setPosts(posts: List<Post>) {
        val diffResult = DiffUtil.calculateDiff(GenericDiffCallback(this.posts, posts))
        this.posts = List(posts.size, { i -> posts[i] })
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }

    inner class HeaderViewHolder internal constructor(containerView: View?, isMe: Boolean, isFollowing: Boolean) : RecyclerView.ViewHolder(containerView) {
        private val editProfile: ImageView? = containerView?.findViewById(R.id.edit_profile)
        private val followUser: ImageView? = containerView?.findViewById(R.id.follow_user)
        private val unfollowUser: ImageView? = containerView?.findViewById(R.id.unfollow_user)
        val name: TextView? = containerView?.findViewById(R.id.name)
        val username: TextView? = containerView?.findViewById(R.id.username)
        val description: TextView? = containerView?.findViewById(R.id.description)

        init {
            if (isMe) {
                editProfile?.visibility = VISIBLE
                followUser?.visibility = GONE
                unfollowUser?.visibility = GONE
                editProfile?.setOnClickListener {
                    onActionExecuted.onNext(Action(
                            Actions.EDIT_PROFILE,
                            mapOf(
                                    "user" to user
                            )
                    ))
                }
            } else {
                if (isFollowing) {
                    unfollowUser?.visibility = VISIBLE
                    editProfile?.visibility = GONE
                    followUser?.visibility = GONE
                    unfollowUser?.setOnClickListener {
                        onActionExecuted.onNext(Action(
                                Actions.UNFOLLOW_USER,
                                emptyMap()
                        ))
                    }
                } else {
                    followUser?.visibility = VISIBLE
                    editProfile?.visibility = GONE
                    unfollowUser?.visibility = GONE
                    followUser?.setOnClickListener {
                        onActionExecuted.onNext(Action(
                                Actions.FOLLOW_USER,
                                emptyMap()
                        ))
                    }
                }
            }
        }
    }
}