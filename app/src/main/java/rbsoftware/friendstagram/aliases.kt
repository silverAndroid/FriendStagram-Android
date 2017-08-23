package rbsoftware.friendstagram

import android.net.Uri
import android.support.v7.widget.Toolbar
import rbsoftware.friendstagram.model.Post

/**
 * Created by Rushil on 8/18/2017.
 */
typealias ImageSelectListener = (Uri) -> Unit
typealias PostSelectListener = (Post) -> Unit
typealias ToolbarManipulator = (Toolbar) -> Unit
typealias ActionExecuteListener = (String) -> Unit