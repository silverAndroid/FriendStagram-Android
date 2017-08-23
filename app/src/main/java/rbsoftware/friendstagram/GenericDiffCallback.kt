package rbsoftware.friendstagram

import android.support.v7.util.DiffUtil

/**
 * Created by Rushil on 8/18/2017.
 */
class GenericDiffCallback<T>(private val oldItems: List<T>, private val newItems: List<T>, private val equalityCheck: (T, T) -> Boolean = { old: T, new: T -> old == new }) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return equalityCheck(oldItems[oldItemPosition], newItems[newItemPosition])
    }

    override fun getOldListSize(): Int {
        return oldItems.size
    }

    override fun getNewListSize(): Int {
        return newItems.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}