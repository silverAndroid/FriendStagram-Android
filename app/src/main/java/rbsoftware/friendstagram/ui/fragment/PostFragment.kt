package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_picture.*
import kotlinx.android.synthetic.main.layout_main_toolbar.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.Post
import rbsoftware.friendstagram.ui.viewholder.PostViewHolder

/**
 * Created by Rushil on 8/18/2017.
 */
class PostFragment: Fragment() {
    private val setToolbar: PublishSubject<Toolbar> = PublishSubject.create()

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

        val pictureView = picture
        val postView = PostViewHolder(pictureView)

        post?.let { postView.init(it) }
        toolbar.let { setToolbar.onNext(it) }
    }

    fun getToolbarManipulator() = setToolbar

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