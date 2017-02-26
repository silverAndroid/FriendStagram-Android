package rbsoftware.friendstagram;

import android.support.v7.widget.Toolbar;

import java.io.Serializable;

/**
 * Created by silver_android on 24/10/16.
 */
public interface ToolbarManipulator extends Serializable {
    public void setToolbar(Toolbar toolbar);
}
