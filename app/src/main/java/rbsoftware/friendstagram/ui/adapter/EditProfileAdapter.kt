package rbsoftware.friendstagram.ui.adapter

import android.support.annotation.DrawableRes
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import rbsoftware.friendstagram.R

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileAdapter : RecyclerView.Adapter<EditProfileAdapter.EditProfileRow>() {
    private val listEditableItems: List<EditProfileItem>

    init {
        listEditableItems = listOf(
                EditProfileItem("Name", R.drawable.ic_account, InputType.TYPE_TEXT_VARIATION_PERSON_NAME),
                EditProfileItem("Username", inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS),
                EditProfileItem("Biography", R.drawable.ic_note_text, InputType.TYPE_TEXT_FLAG_MULTI_LINE, maxLines = 3),
                EditProfileItem("Current Password", R.drawable.ic_lock, InputType.TYPE_TEXT_VARIATION_PASSWORD),
                EditProfileItem("New Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD),
                EditProfileItem("Verify Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EditProfileRow {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_edit_profile, parent, false)
        return EditProfileRow(view)
    }

    override fun onBindViewHolder(holder: EditProfileRow?, position: Int) {
        val item: EditProfileItem = listEditableItems[position]

        holder?.hint?.hint = item.hint
        holder?.input?.inputType = item.inputType
        holder?.input?.maxLines = item.maxLines
        item.icon?.let { holder?.icon?.setImageResource(it) }
    }

    override fun getItemCount(): Int = listEditableItems.size

    inner class EditProfileRow(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView? = itemView?.findViewById(R.id.icon)
        val input: EditText? = itemView?.findViewById(R.id.input)
        val hint: TextInputLayout? = itemView?.findViewById(R.id.input_hint)
    }

    data class EditProfileItem(val hint: String, @DrawableRes val icon: Int? = null, val inputType: Int = InputType.TYPE_CLASS_TEXT, val maxLines: Int = 1)
}