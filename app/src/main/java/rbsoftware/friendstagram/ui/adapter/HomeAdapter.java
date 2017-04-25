package rbsoftware.friendstagram.ui.adapter;

import android.app.Activity;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rbsoftware.friendstagram.ui.viewholder.PostViewHolder;
import rbsoftware.friendstagram.R;
import rbsoftware.friendstagram.model.Post;

/**
 * Created by silver_android on 07/10/16.
 */

public class HomeAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private ArrayList<Post> posts;
    private final Activity activity;

    public HomeAdapter(ArrayList<Post> posts, Activity activity) {
        this.posts = posts;
        this.activity = activity;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.init(post);
    }

    public void setPosts(List<Post> posts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostsDiffCallback(this.posts, posts));
        this.posts.clear();
        this.posts.addAll(posts);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    private class PostsDiffCallback extends DiffUtil.Callback {

        private final List<Post> oldPosts;
        private final List<Post> newPosts;

        PostsDiffCallback(List<Post> oldPosts, List<Post> newPosts) {
            this.oldPosts = oldPosts;
            this.newPosts = newPosts;
        }

        @Override
        public int getOldListSize() {
            return oldPosts.size();
        }

        @Override
        public int getNewListSize() {
            return newPosts.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).getID() == newPosts.get(newItemPosition).getID();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}
