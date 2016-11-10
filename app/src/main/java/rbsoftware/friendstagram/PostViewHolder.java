package rbsoftware.friendstagram;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import rbsoftware.friendstagram.model.Post;

/**
 * Created by silver_android on 17/10/16.
 */

class PostViewHolder extends RecyclerView.ViewHolder {

    SimpleDraweeView profilePicture;
    SimpleDraweeView image;
    TextView username;

    PostViewHolder(View itemView) {
        super(itemView);
        profilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile);
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        username = (TextView) itemView.findViewById(R.id.username);
    }

    public void init(Post post) {
        Uri imageURI = Uri.parse(post.getImageURL());
        Uri profilePicURI = Uri.parse(post.getUser().getProfilePictureURL());

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(new ResizeOptions(500, 500))
                .build();
        PipelineDraweeController imageController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(image.getController())
                .setImageRequest(imageRequest)
                .build();
        image.setController(imageController);

        ImageRequest profilePicRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        PipelineDraweeController profilePicController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(profilePicture.getController())
                .setImageRequest(profilePicRequest)
                .build();
        profilePicture.setController(profilePicController);

        username.setText(post.getUser().getUsername());
        image.setImageURI(imageURI);
        profilePicture.setImageURI(profilePicURI);
    }
}