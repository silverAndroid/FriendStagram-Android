package rbsoftware.friendstagram.ui.adapter

import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import io.reactivex.subjects.PublishSubject
import rbsoftware.friendstagram.Constants
import rbsoftware.friendstagram.GenericDiffCallback
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.databinding.HeaderProfileBinding
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.ui.viewholder.PictureViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class ProfileAdapter(private var posts: List<Post>, private var user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val onPostSelected: PublishSubject<Post> = PublishSubject.create()
    private val onActionExecuted: PublishSubject<String> = PublishSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val dataBindingView: HeaderProfileBinding = DataBindingUtil.inflate(LayoutInflater.from(parent?.context), R.layout.header_profile, parent, false)
            HeaderViewHolder(dataBindingView)
        } else {
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_image, parent, false)
            PictureViewHolder(view)
        }
    }

    override fun onBindViewHolder(parent: RecyclerView.ViewHolder?, position: Int) {
        if (isHeader(position)) {
            val holder = parent as HeaderViewHolder
            holder.profileBinding.user = user
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

    fun getOnActionExecuted(): PublishSubject<String> = onActionExecuted

    private fun setPosts(posts: List<Post>) {
        val diffResult = DiffUtil.calculateDiff(GenericDiffCallback(this.posts, posts))
        this.posts = List(posts.size, { i -> posts[i] })
        diffResult.dispatchUpdatesTo(this)
    }

    companion object {
        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }

    inner class HeaderViewHolder internal constructor(internal val profileBinding: HeaderProfileBinding) : RecyclerView.ViewHolder(profileBinding.root) {
        private val editProfile: ImageView

        init {
            this.profileBinding.executePendingBindings()
            editProfile = profileBinding.root.findViewById(R.id.edit_profile)
            editProfile.setOnClickListener {
                onActionExecuted.onNext(Constants.Action.EDIT_PROFILE)
            }
        }
    }
}