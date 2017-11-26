package rbsoftware.friendstagram.ui.viewholder

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.common.ResizeOptions
import com.facebook.imagepipeline.request.ImageRequestBuilder
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Post

/**
 * Created by silver_android on 17/10/16.
 */

class PostViewHolder(containerView: View?) : RecyclerView.ViewHolder(containerView) {
    private val username: TextView? = containerView?.findViewById(R.id.username)
    private val image: SimpleDraweeView? = containerView?.findViewById(R.id.image)
    private val profilePicture: SimpleDraweeView? = containerView?.findViewById(R.id.profile)

    fun init(post: Post) {
        val imageURI = Uri.parse(post.imageURL)
        val imageRequest = ImageRequestBuilder.newBuilderWithSource(imageURI)
                .setResizeOptions(ResizeOptions(500, 500))
                .build()
        val imageController = Fresco.newDraweeControllerBuilder()
                .setOldController(image?.controller)
                .setImageRequest(imageRequest)
                .build() as PipelineDraweeController

        val profilePicURI = Uri.parse(post.user.profilePictureURL)
        val profilePicRequest = ImageRequestBuilder.newBuilderWithSource(profilePicURI)
                .setResizeOptions(ResizeOptions(300, 300))
                .build()
        val profilePicController = Fresco.newDraweeControllerBuilder()
                .setOldController(profilePicture?.controller)
                .setImageRequest(profilePicRequest)
                .build() as PipelineDraweeController

        image?.controller = imageController
        image?.setImageURI(imageURI)
        profilePicture?.controller = profilePicController
        profilePicture?.setImageURI(profilePicURI)
        username?.text = post.user.username
    }
}