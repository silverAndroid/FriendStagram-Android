package rbsoftware.friendstagram.ui.adapter;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import rbsoftware.friendstagram.ActionExecuteListener;
import rbsoftware.friendstagram.Constants;
import rbsoftware.friendstagram.ui.viewholder.PictureViewHolder;
import rbsoftware.friendstagram.R;
import rbsoftware.friendstagram.databinding.HeaderProfileBinding;
import rbsoftware.friendstagram.model.Post;
import rbsoftware.friendstagram.model.User;

/**
 * Created by silver_android on 13/10/16.
 */

public class ProfileAdapter extends RecyclerView.Adapter {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private User user;
    private ArrayList<Post> posts;
    private final ImageClickListener listener;
    private final ActionExecuteListener actionExecuteListener;

    public ProfileAdapter(ImageClickListener imageClickListener, ActionExecuteListener actionExecuteListener) {
        listener = imageClickListener;
        this.actionExecuteListener = actionExecuteListener;
        posts = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            HeaderProfileBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.header_profile, parent, false);
            return new HeaderViewHolder(viewDataBinding, actionExecuteListener);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder parent, int position) {
        if (isHeader(position)) {
            HeaderViewHolder holder = (HeaderViewHolder) parent;
            holder.itemView.setUser(user);
        } else {
            final PictureViewHolder holder = (PictureViewHolder) parent;
            int index = position - (user == null ? 0 : 1);
            final Post post = posts.get(index);
            final boolean[] imageLoaded = {false};
            holder.image.setImageURI(Uri.parse(post.getImageURL()));
            imageLoaded[0] = true;
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageLoaded[0]) {
                        listener.onImageClick(post);
                    }
                }
            });
        }
    }

    public void setPosts(ArrayList<Post> posts) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new PostsDiffCallback(this.posts, posts));
        this.posts.clear();
        this.posts.addAll(posts);
        diffResult.dispatchUpdatesTo(this);
    }

    @Override
    public int getItemCount() {
        return posts.size() + (user != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    public void setUser(User user) {
        boolean hadNoUser = this.user == null;
        this.user = user;

        if (hadNoUser)
            notifyItemInserted(0);
        else
            notifyItemChanged(0);

        setPosts(user.getPosts());
    }

    public boolean isHeader(int position) {
        return user != null && position == 0;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private static final String TAG = "HeaderViewHolder";
        private final HeaderProfileBinding itemView;
        private final ActionExecuteListener listener;
        private final ImageView editProfile;

        HeaderViewHolder(HeaderProfileBinding itemView, ActionExecuteListener listener) {
            super(itemView.getRoot());
            this.itemView = itemView;
            this.listener = listener;
            this.itemView.executePendingBindings();
            editProfile = (ImageView) itemView.getRoot().findViewById(R.id.edit_profile);
            editProfile.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "editProfileClick: Edit Profile");
            listener.runAction(Constants.Action.EDIT_PROFILE);
        }
    }

    public interface ImageClickListener {
        void onImageClick(Post post);
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
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldPosts.get(oldItemPosition).equals(newPosts.get(newItemPosition));
        }
    }
}
