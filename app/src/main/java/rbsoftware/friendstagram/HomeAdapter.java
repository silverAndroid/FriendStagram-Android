package rbsoftware.friendstagram;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.util.ArrayList;

import rbsoftware.friendstagram.model.Post;

/**
 * Created by silver_android on 07/10/16.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<Post> posts;

    public HomeAdapter(ArrayList<Post> posts) {
        this.posts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Post post = posts.get(position);

        Uri imageURI = Uri.parse(post.getImageURL());
        Uri profilePicURI = Uri.parse(post.getUser().getProfilePictureURL());

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(new ResizeOptions(500, 500))
                .build();
        PipelineDraweeController imageController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.image.getController())
                .setImageRequest(imageRequest)
                .build();
        holder.image.setController(imageController);

        ImageRequest profilePicRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        PipelineDraweeController profilePicController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(holder.profilePicture.getController())
                .setImageRequest(profilePicRequest)
                .build();
        holder.profilePicture.setController(profilePicController);

        holder.username.setText(post.getUser().getUsername());
        holder.image.setImageURI(imageURI);
        holder.profilePicture.setImageURI(profilePicURI);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView profilePicture;
        SimpleDraweeView image;
        TextView username;

        ViewHolder(View itemView) {
            super(itemView);
            profilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile);
            image = (SimpleDraweeView) itemView.findViewById(R.id.image);
            username = (TextView) itemView.findViewById(R.id.username);
        }
    }
}
