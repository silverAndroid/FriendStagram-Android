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
import rbsoftware.friendstagram.validate
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileAdapter : RecyclerView.Adapter<EditProfileAdapter.EditProfileRow>() {
    private val editableItems: List<EditProfileItem>

    init {
        editableItems = listOf(
                EditProfileItem("Name", R.drawable.ic_account, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, listOf(Validator.empty())),
                EditProfileItem("Username", inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, validators = listOf(Validator.empty())),
                EditProfileItem("Biography", R.drawable.ic_note_text, InputType.TYPE_TEXT_FLAG_MULTI_LINE, maxLines = 3, key = "bio"),
                EditProfileItem("Current Password", R.drawable.ic_lock, InputType.TYPE_TEXT_VARIATION_PASSWORD, listOf(Validator.empty(), Validator.passwordValid()), key = "password.old"),
                EditProfileItem("New Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.empty(), Validator.passwordValid()), key = "password.new"),
                EditProfileItem("Verify Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.empty(), Validator.passwordValid()), key = null)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): EditProfileRow {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_edit_profile, parent, false)
        return EditProfileRow(view)
    }

    override fun onBindViewHolder(holder: EditProfileRow?, position: Int) {
        val item: EditProfileItem = editableItems[position]

        with(item) {
            holder?.hint?.hint = hint
            holder?.input?.inputType = inputType
            holder?.input?.maxLines = maxLines
            holder?.hint?.id = uniqueID
        }

        item.icon?.let { holder?.icon?.setImageResource(it) }
        holder?.hint?.let { editableItems[position].input = it }
        item.validators.forEach {
            Validators.addValidation(item.uniqueID, it)
        }
    }

    override fun getItemCount(): Int = editableItems.size

    fun getData(): Map<String, Any>? {
        var isValid = true
        editableItems.forEach { editProfileItem ->
            editProfileItem.validators.forEach {
                isValid = editProfileItem.input.validate() && isValid
            }
        }
        return if (isValid) {
            createNestedMap(editableItems.map { it.getMap() })
        } else {
            null
        }
    }

    private fun createNestedMap(mapList: List<Map<String, Any>>): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = mutableMapOf()
        mapList.forEach { mapObj ->
            mapObj.keys.forEach { key ->
                if (mapObj[key] is String) {
                    map[key] = mapObj[key] as String
                } else {
                    map[key] = addToMap(mapObj.toMutableMap(), map, key)
                }
            }
        }
        return map
    }

    private fun createNestedMap(mutableMap: MutableMap<String, Any>): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = mutableMapOf()
        mutableMap.keys.forEach { key ->
            if (mutableMap[key] is String) {
                map[key] = mutableMap[key] as String
            } else {
                map[key] = addToMap(mutableMap, map, key)
            }
        }
        return map
    }

    private fun addToMap(map: MutableMap<String, Any>, returnMap: MutableMap<String, Any>, key: String): MutableMap<String, Any> {
        val nestedMap = createNestedMap(map[key] as MutableMap<String, Any>)
        return if (returnMap[key] == null) {
            nestedMap
        } else {
            (returnMap[key] as MutableMap<String, Any>).plus(nestedMap).toMutableMap()
        }
    }

    inner class EditProfileRow(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView? = itemView?.findViewById(R.id.icon)
        val input: EditText? = itemView?.findViewById(R.id.input)
        val hint: TextInputLayout? = itemView?.findViewById(R.id.input_hint)
    }

    data class EditProfileItem(val hint: String, @DrawableRes val icon: Int? = null, val inputType: Int = InputType.TYPE_CLASS_TEXT, val validators: List<Validator> = listOf(), private val key: String? = hint.toLowerCase(), val maxLines: Int = 1) {
        val uniqueID: Int = generateViewId()
        lateinit var input: TextInputLayout

        fun getMap(): MutableMap<String, Any> {
            var map = mutableMapOf<String, Any>()
            key?.let {
                it.split(".")
                        .asReversed()
                        .forEach {
                            map = if (map.isEmpty()) {
                                mutableMapOf(it to input.editText?.text.toString())
                            } else {
                                mutableMapOf(it to map)
                            }
                        }
            }
            return map
        }

        private fun generateViewId(): Int {
            while (true) {
                val result = sNextGeneratedId.get()
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                var newValue = result + 1
                if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result
                }
            }
        }

        companion object {
            @JvmStatic
            private val sNextGeneratedId = AtomicInteger(1)
        }
    }
}