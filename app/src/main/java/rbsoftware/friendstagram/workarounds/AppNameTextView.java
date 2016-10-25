package rbsoftware.friendstagram.workarounds;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by silver_android on 25/10/16.
 */

public class AppNameTextView extends TextView {
    public AppNameTextView(Context context) {
        super(context);
        init();
    }

    public AppNameTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppNameTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), "billabong.ttf");
            setTypeface(font);
        }
    }
}
