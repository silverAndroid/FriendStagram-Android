package rbsoftware.friendstagram.ui.viewholder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.facebook.drawee.view.SimpleDraweeView
import rbsoftware.friendstagram.R

/**
 * Created by silver_android on 09/11/16.
 */

class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var image: SimpleDraweeView = itemView.findViewById(R.id.image)
}
