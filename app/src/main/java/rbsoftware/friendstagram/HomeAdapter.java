package rbsoftware.friendstagram;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import rbsoftware.friendstagram.model.Post;

/**
 * Created by silver_android on 07/10/16.
 */

public class HomeAdapter extends RecyclerView.Adapter<PostViewHolder> {

    private ArrayList<Post> posts;

    public HomeAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.init(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
