package rbsoftware.friendstagram.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.facebook.drawee.view.SimpleDraweeView
import rbsoftware.friendstagram.R

/**
 * Created by Rushil on 8/23/2017.
 */
class SharePostFragment: Fragment() {
    private var imageURI: Uri? = null
    private var editCaption: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageURI = arguments.getParcelable(ARG_IMAGE_URI)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_share_post, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val image: SimpleDraweeView? = view?.findViewById(R.id.image)
        editCaption = view?.findViewById(R.id.caption)

        image?.setImageURI(imageURI)
    }

    fun getCaption(): String {
        return editCaption?.text.toString()
    }

    fun showRequiredCaption() {
        editCaption?.error = getString(R.string.error_field_required)
        editCaption?.requestFocus()
    }

    companion object {
        private val ARG_IMAGE_URI = "image_uri"

        fun newInstance(uri: Uri): SharePostFragment {
            val fragment = SharePostFragment()
            val args = Bundle()
            args.putParcelable(ARG_IMAGE_URI, uri)
            fragment.arguments = args
            return fragment
        }
    }
}