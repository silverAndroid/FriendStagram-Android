package rbsoftware.friendstagram.ui.adapter

import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import rbsoftware.friendstagram.GenericDiffCallback
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.inflate
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.ui.viewholder.PostViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class HomeAdapter(private var posts: List<Post>) : RecyclerView.Adapter<PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PostViewHolder {
        val view = parent?.inflate(R.layout.item_post)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder?, position: Int) {
        val post = posts[position]
        holder?.init(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun setPosts(posts: List<Post>) {
        val diffResult = DiffUtil.calculateDiff(GenericDiffCallback(this.posts, posts, equalityCheck = { oldPost, newPost -> oldPost.id == newPost.id }))
        this.posts = List(posts.size, { i -> posts[i] })
        diffResult.dispatchUpdatesTo(this)
    }
}