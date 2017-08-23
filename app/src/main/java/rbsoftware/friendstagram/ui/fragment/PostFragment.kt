package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.ToolbarManipulator
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.ui.viewholder.PostViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class PostFragment: Fragment() {
    private lateinit var setToolbar: ToolbarManipulator
    private var toolbar: Toolbar? = null
    private var post: Post? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        post = arguments.getParcelable<Post>(ARG_POST) as Post
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_picture, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = view?.findViewById(R.id.toolbar)
        val pictureView: View? = view?.findViewById(R.id.picture)
        val picture = PostViewHolder(pictureView)

        post?.let { picture.init(it) }
        toolbar?.let { setToolbar(it) }
    }

    fun setToolbarManipulator(toolbarManipulator: ToolbarManipulator) {
        this.setToolbar = toolbarManipulator
        toolbar?.let { setToolbar(it) }
    }

    companion object {
        private const val ARG_POST = "post"

        fun newInstance(post: Post): PostFragment {
            val fragment = PostFragment()
            val args = Bundle()
            args.putParcelable(ARG_POST, post)
            fragment.arguments = args
            return fragment
        }
    }
}