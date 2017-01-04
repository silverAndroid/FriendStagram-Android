package rbsoftware.friendstagram;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import rbsoftware.friendstagram.model.Picture;

/**
 * Created by silver_android on 09/11/16.
 */

public class PicturesAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private static final String TAG = "PicturesAdapter";
    private ArrayList<Picture> images;

    public PicturesAdapter(Cursor cursor) {
        images = new ArrayList<>();
        changeCursor(cursor);
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        holder.image.setImageURI(Uri.parse(images.get(position).getURL()));
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
                images.add(new Picture(cursor.getInt(idColumnIndex), "file://" + cursor.getString(dataColumnIndex)));
            } while (cursor.moveToNext());
        }

        notifyDataSetChanged();
    }
}
