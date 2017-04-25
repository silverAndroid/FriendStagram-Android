package rbsoftware.friendstagram;

import android.app.Activity;
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
import rbsoftware.friendstagram.service.ImageService;

/**
 * Created by silver_android on 17/10/16.
 */

class PostViewHolder extends RecyclerView.ViewHolder {

    private final Activity activity;
    private SimpleDraweeView profilePicture;
    private SimpleDraweeView image;
    private TextView username;

    PostViewHolder(View itemView, Activity activity) {
        super(itemView);
        profilePicture = (SimpleDraweeView) itemView.findViewById(R.id.profile);
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
        username = (TextView) itemView.findViewById(R.id.username);
        this.activity = activity;
    }

    public void init(final Post post) {
        final Uri imageURI = Uri.parse(post.getImageURL());

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(new ResizeOptions(500, 500))
                .build();
        final PipelineDraweeController imageController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(image.getController())
                .setImageRequest(imageRequest)
                .build();

        image.setController(imageController);
        image.setImageURI(imageURI);

        Uri profilePicURI = Uri.parse(
                "http://tracara.com/wp-content/uploads/2016/04/aleksandar-radojicic-i-aja-e1461054273916.jpg?fa0c3d"
                /*post.getUser().getProfilePictureURL()*/
        );

        ImageRequest profilePicRequest = ImageRequestBuilder.newBuilderWithSource(profilePicURI)
                .setResizeOptions(new ResizeOptions(300, 300))
                .build();
        PipelineDraweeController profilePicController = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setOldController(profilePicture.getController())
                .setImageRequest(profilePicRequest)
                .build();
        profilePicture.setController(profilePicController);

        username.setText(post.getUser().getUsername());
        profilePicture.setImageURI(profilePicURI);
    }
}