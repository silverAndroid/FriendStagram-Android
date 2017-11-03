package rbsoftware.friendstagram.ui.adapter

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.item_image.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.inflate
import rbsoftware.friendstagram.model.Picture
import rbsoftware.friendstagram.ui.viewholder.PictureViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class PicturesAdapter(cursor: Cursor, private val context: Context) : RecyclerView.Adapter<PictureViewHolder>() {
    private val onImageSelected: PublishSubject<Uri> = PublishSubject.create()

    private var images: List<Picture> = listOf()
    private var selectedPosition: Int = 0

    init {
        changeCursor(cursor)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): PictureViewHolder {
        val view = parent?.inflate(R.layout.item_image)
        return PictureViewHolder(view)
    }

    override fun onBindViewHolder(holder: PictureViewHolder?, position: Int) {
        holder?.image?.setImageURI(images[position].uri)
        holder?.image?.setOnClickListener {
            val oldPosition: Int = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)
            onImageSelected.onNext(images[selectedPosition].uri)
        }

        if (selectedPosition == position) {
            holder?.image?.setColorFilter(ContextCompat.getColor(context, R.color.colorTranslucentOverlay3))
        } else {
            holder?.image?.colorFilter = null
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun changeCursor(cursor: Cursor?) {
        images = if (cursor?.moveToFirst() == true) {
            val dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val idColumnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID)

            List(cursor.count, {
                val data = Uri.parse("file://${cursor.getString(dataColumnIndex)}")
                val id = cursor.getInt(idColumnIndex)

                cursor.moveToNext()
                Picture(id, data)
            })
        } else {
            listOf()
        }

        notifyDataSetChanged()
    }

    fun getOnImageSelected(): PublishSubject<Uri> = onImageSelected
}