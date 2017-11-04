package rbsoftware.friendstagram.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import rbsoftware.friendstagram.R

/**
 * Created by silver_android on 09/11/16.
 */

class PictureViewHolder(containerView: View?) : RecyclerView.ViewHolder(containerView) {
    val image: ImageView? = containerView?.findViewById(R.id.image)
}
