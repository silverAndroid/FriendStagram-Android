package rbsoftware.friendstagram.ui.adapter

import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.RecyclerView
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import com.facebook.drawee.view.SimpleDraweeView
import io.reactivex.subjects.PublishSubject
import rbsoftware.friendstagram.*
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.model.Validator
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileAdapter(private val user: User) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val ITEM_VIEW_TYPE_PICTURES = 0
    private val ITEM_VIEW_TYPE_INPUT = 1
    private val editableItems: List<EditProfileItem>

    private var profileURL: String?
    private var backgroundURL: String?

    val imageClickListener: PublishSubject<Int> = PublishSubject.create()

    init {
        editableItems = listOf(
                EditProfileItem("Name", user.name, R.drawable.ic_account, InputType.TYPE_TEXT_VARIATION_PERSON_NAME, listOf(Validator.empty())),
                EditProfileItem("Username", user.username, inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS, validators = listOf(Validator.empty())),
                EditProfileItem("Biography", user.biography
                        ?: "", R.drawable.ic_note_text, InputType.TYPE_TEXT_FLAG_MULTI_LINE, maxLines = 3, key = "description"),
                EditProfileItem("Current Password", icon = R.drawable.ic_lock, inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.empty(), Validator.passwordValid()), key = "old_password"),
                EditProfileItem("New Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.notRequired(), Validator.empty(), Validator.passwordValid()), key = "new_password"),
                EditProfileItem("Verify Password", inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD, validators = listOf(Validator.notRequired(), Validator.empty(), Validator.passwordValid()), key = null)
        )

        profileURL = user.profilePictureURL
        backgroundURL = user.backgroundPictureURL
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_VIEW_TYPE_PICTURES) {
            val view = parent?.inflate(R.layout.item_edit_picture)
            EditPictureRow(view)
        } else {
            val view = parent?.inflate(R.layout.item_edit_profile)
            EditProfileRow(view)
        }
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder?, position: Int) {
        val inputPosition = position - 1

        if (holderParent is EditProfileRow) {
            val item: EditProfileItem = editableItems[inputPosition]
            val holder: EditProfileRow = holderParent
            with(item) {
                holder.hint?.hint = hint
                holder.input?.setText(value)
                holder.input?.maxLines = maxLines
                holder.hint?.id = uniqueID
                holder.input?.setInputView(inputType)
            }

            item.icon?.let { holder.icon?.setImageResource(it) }
            holder.hint?.let { editableItems[inputPosition].input = it }
            item.validators.forEach {
                Validators.addValidation(item.uniqueID, it)
            }
        } else if (holderParent is EditPictureRow) {
            val holder: EditPictureRow = holderParent

            holder.background?.setImageURI(backgroundURL)
            holder.profile?.setImageURI(profileURL)
        }
    }

    override fun getItemCount(): Int = editableItems.size + 1

    override fun getItemViewType(position: Int): Int = if (position == 0) {
        ITEM_VIEW_TYPE_PICTURES
    } else {
        ITEM_VIEW_TYPE_INPUT
    }

    fun updateImage(uri: Uri, imageID: Int) {
        if (imageID == R.id.profile) profileURL = uri.toString()
        else if (imageID == R.id.background) backgroundURL = uri.toString()
        notifyItemChanged(0)
    }

    fun getData(): Map<String, Any>? {
        var isValid = true
        editableItems.forEach { editProfileItem ->
            val validators = editProfileItem.validators
            validators
                    .takeWhile { !(it == Validator.notRequired() && editProfileItem.input.editText!!.text.isEmpty()) }
                    .forEach { isValid = editProfileItem.input.validate() && isValid }
        }
        return if (isValid) {
            val map = createNestedMap(editableItems.map { it.getMap() }).toMutableMap()
            profileURL?.let { map["profile_picture_url"] = it }
            backgroundURL?.let { map["profile_background_url"] = it }
            map
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

    inner class EditPictureRow(containerView: View?) : RecyclerView.ViewHolder(containerView) {
        val profile: SimpleDraweeView? = containerView?.findViewById(R.id.profile)
        val background: SimpleDraweeView? = containerView?.findViewById(R.id.background)

        init {
            profile?.setOnClickListener {
                imageClickListener.onNext(profile.id)
            }
            background?.setOnClickListener {
                imageClickListener.onNext(background.id)
            }
        }
    }

    inner class EditProfileRow(containerView: View?) : RecyclerView.ViewHolder(containerView) {
        val icon: ImageView? = containerView?.findViewById(R.id.icon)
        val input: EditText? = containerView?.findViewById(R.id.input)
        val hint: TextInputLayout? = containerView?.findViewById(R.id.input_hint)
    }

    data class EditProfileItem(val hint: String, val value: String = "", @DrawableRes val icon: Int? = null, val inputType: Int = InputType.TYPE_CLASS_TEXT, val validators: List<Validator> = emptyList(), private val key: String? = hint.toLowerCase(), val maxLines: Int = 1) {
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