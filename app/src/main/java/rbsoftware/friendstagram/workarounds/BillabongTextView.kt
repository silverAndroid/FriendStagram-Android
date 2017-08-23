package rbsoftware.friendstagram.workarounds

import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView

/**
 * Created by silver_android on 25/10/16.
 */

class BillabongTextView(context: Context) : AppCompatTextView(context) {
    init {
        if (!isInEditMode) {
            val font = Typeface.createFromAsset(context.assets, "billabong.ttf")
            typeface = font
        }
    }
}
