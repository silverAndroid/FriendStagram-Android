package rbsoftware.friendstagram.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.drawee.view.SimpleDraweeView
import rbsoftware.friendstagram.R

/**
 * Created by Rushil on 8/23/2017.
 */
class SelectFilterFragment: Fragment() {
    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageURI = arguments.getParcelable(ARG_IMAGE_URI)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_select_filter, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image: SimpleDraweeView? = view?.findViewById(R.id.image)

        image?.setImageURI(imageURI)
    }

    companion object {
        private val ARG_IMAGE_URI = "image_uri"

        fun newInstance(uri: Uri): SelectFilterFragment {
            val fragment = SelectFilterFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE_URI, uri)
            fragment.arguments = args
            return fragment
        }
    }
}