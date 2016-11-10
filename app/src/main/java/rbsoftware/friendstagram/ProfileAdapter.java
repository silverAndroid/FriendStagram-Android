package rbsoftware.friendstagram;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> imageURLs;
    private ImageClickListener listener;

    public ProfileAdapter(User user, ArrayList<String> imageURLs, ImageClickListener imageClickListener) {
        this.user = user;
        this.imageURLs = imageURLs;
        listener = imageClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            HeaderProfileBinding viewDataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.header_profile, parent, false);
            return new HeaderViewHolder(viewDataBinding);
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
            PictureViewHolder holder = (PictureViewHolder) parent;
            final String imageURL = imageURLs.get(position - 1);
            holder.image.setImageURI(Uri.parse(imageURL));
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = new Post(imageURL, "", user);
                    listener.onImageClick(post);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageURLs.size() + (user != null ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    boolean isHeader(int position) {
        return user != null && position == 0;
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final HeaderProfileBinding itemView;

        HeaderViewHolder(HeaderProfileBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
            this.itemView.executePendingBindings();
        }
    }

    interface ImageClickListener {
        void onImageClick(Post post);
    }
}
