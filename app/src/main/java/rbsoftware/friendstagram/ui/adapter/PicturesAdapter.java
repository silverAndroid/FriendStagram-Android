package rbsoftware.friendstagram.ui.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import rbsoftware.friendstagram.ImageSelectListener;
import rbsoftware.friendstagram.ui.viewholder.PictureViewHolder;
import rbsoftware.friendstagram.R;
import rbsoftware.friendstagram.model.Picture;

/**
 * Created by silver_android on 09/11/16.
 */

public class PicturesAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private static final String TAG = "PicturesAdapter";
    private ArrayList<Picture> images;
    private int selectedPosition;
    private Context context;
    private final ImageSelectListener imageSelectListener;

    public PicturesAdapter(Cursor cursor, Context context, ImageSelectListener imageSelectListener) {
        this.context = context;
        this.imageSelectListener = imageSelectListener;
        images = new ArrayList<>();
        changeCursor(cursor);
        selectedPosition = 0;
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PictureViewHolder holder, int position) {
        holder.image.setImageURI(images.get(position).getURI());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);
                notifyItemChanged(oldSelectedPosition);
                imageSelectListener.onImageSelected(images.get(selectedPosition).getURI());
            }
        });

        if (selectedPosition == position) {
            holder.image.setColorFilter(ContextCompat.getColor(context, R.color.colorTranslucentOverlay3));
        } else {
            holder.image.setColorFilter(null);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public void changeCursor(Cursor cursor) {
        images = new ArrayList<>();
        Log.i(TAG, "changeCursor: Resetting adapter");
        if (cursor != null && cursor.moveToFirst()) {
            Log.i(TAG, "changeCursor: Adding images");
            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID);

            do {
                images.add(new Picture(cursor.getInt(idColumnIndex), Uri.parse("file://" + cursor.getString(dataColumnIndex))));
            } while (cursor.moveToNext());
        }

        notifyDataSetChanged();

        if (images.size() > selectedPosition) {
            imageSelectListener.onImageSelected(images.get(selectedPosition).getURI());
        } else {
            imageSelectListener.onImageSelected(null);
        }
    }
}
