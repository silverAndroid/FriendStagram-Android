package rbsoftware.friendstagram.ui.adapter

import android.net.Uri
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.header_profile.*
import kotlinx.android.synthetic.main.item_image.*
import rbsoftware.friendstagram.Constants
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
class ProfileAdapter(private var posts: List<Post>, private var user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val onPostSelected: PublishSubject<Post> = PublishSubject.create()
    private val onActionExecuted: PublishSubject<Action> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val view = parent?.inflate(R.layout.header_profile)
            HeaderViewHolder(view)
        } else {
            val view = parent?.inflate(R.layout.item_image)
            PictureViewHolder(view)
        }
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder?, position: Int) {
        if (isHeader(position)) {
            val holder = parent as HeaderViewHolder
            with(user) {
                holder.name.text = name
                holder.username.text = username
                holder.description.text = description
            }
        } else {
            val holder = parent as PictureViewHolder
            val post = posts[position - 1]
            holder.image.setImageURI(Uri.parse(post.imageURL))
            holder.image.setOnClickListener { onPostSelected.onNext(post) }
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

    fun setUser(user: User) {
        this.user = user
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

    inner class HeaderViewHolder internal constructor(override val containerView: View?) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private val editProfile: ImageView = edit_profile

        init {
            editProfile.setOnClickListener {
                onActionExecuted.onNext(Action(
                        Constants.Action.EDIT_PROFILE,
                        mapOf(
                                "user" to user
                        )
                ))
            }
        }
    }
}