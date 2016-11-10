package rbsoftware.friendstagram;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by silver_android on 09/11/16.
 */

public class PictureViewHolder extends RecyclerView.ViewHolder {
    SimpleDraweeView image;

    PictureViewHolder(View itemView) {
        super(itemView);
        image = (SimpleDraweeView) itemView.findViewById(R.id.image);
    }
}
