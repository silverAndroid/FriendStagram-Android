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
import rbsoftware.friendstagram.Validators
import rbsoftware.friendstagram.model.Validator

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileAdapter : RecyclerView.Adapter<EditProfileAdapter.EditProfileRow>() {
    private val editableItems: List<EditProfileItem>

    init {
        editableItems = listOf(
                EditProfileItem("Name", R.drawable.ic_account, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, listOf(Validator.empty())),
                EditProfileItem("Username", inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, validators = listOf(Validator.empty())),
                EditProfileItem("Biography", R.drawable.ic_note_text, InputType.TYPE_TEXT_FLAG_MULTI_LINE, maxLines = 3),
                EditProfileItem("Current Password", R.drawable.ic_lock, InputType.TYPE_TEXT_VARIATION_PASSWORD, listOf(Validator.empty(), Validator.passwordValid())),
                EditProfileItem("New Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.empty(), Validator.passwordValid())),
                EditProfileItem("Verify Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.empty(), Validator.passwordValid()))
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EditProfileRow {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_edit_profile, parent, false)
        return EditProfileRow(view)
    }

    override fun onBindViewHolder(holder: EditProfileRow?, position: Int) {
        val item: EditProfileItem = editableItems[position]

        with (item) {
            holder?.hint?.hint = hint
            holder?.input?.inputType = inputType
            holder?.input?.maxLines = maxLines
        }

        item.icon?.let { holder?.icon?.setImageResource(it) }
        holder?.input?.let { input ->
            item.validators.forEach {
                Validators.addValidation(input.id, it)
            }
        }
    }

    override fun getItemCount(): Int = editableItems.size

    inner class EditProfileRow(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView? = itemView?.findViewById(R.id.icon)
        val input: EditText? = itemView?.findViewById(R.id.input)
        val hint: TextInputLayout? = itemView?.findViewById(R.id.input_hint)
    }

    data class EditProfileItem(val hint: String, @DrawableRes val icon: Int? = null, val inputType: Int = InputType.TYPE_CLASS_TEXT, val validators: List<Validator> = listOf(), val maxLines: Int = 1)
}