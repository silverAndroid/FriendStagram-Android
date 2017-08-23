package rbsoftware.friendstagram.workarounds

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Created by silver_android on 25/10/16.
 */

class BillabongTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    init {
        if (!isInEditMode) {
            val font = Typeface.createFromAsset(context.assets, "billabong.ttf")
            typeface = font
        }
    }
}
