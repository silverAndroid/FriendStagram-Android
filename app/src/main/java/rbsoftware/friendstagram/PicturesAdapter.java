package rbsoftware.friendstagram;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by silver_android on 09/11/16.
 */

public class PicturesAdapter extends RecyclerView.Adapter<PictureViewHolder> {

    private ArrayList<String> imageURLs;

    public PicturesAdapter(Cursor cursor) {
        changeCursor(cursor);
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new PictureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        holder.image.setImageURI(Uri.parse(imageURLs.get(position)));
    }

    @Override
    public int getItemCount() {
        return imageURLs.size();
    }

    public void changeCursor(Cursor cursor) {
        imageURLs = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                imageURLs.add(cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
            } while (cursor.moveToNext());
        }
    }
}
