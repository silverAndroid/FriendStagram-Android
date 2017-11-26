package rbsoftware.friendstagram.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_setup.*
import rbsoftware.friendstagram.BuildConfig
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.inflate

/**
 * A simple [Fragment] subclass.
 */
class SetupFragment : Fragment() {
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        username = arguments.getString(ARG_USERNAME)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return container?.inflate(R.layout.fragment_setup, false, inflater)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnDefaultPicture.visibility = View.GONE
        profile.setImageURI(Uri.parse("${BuildConfig.BASE_URL}/users/default_profile_picture?username=$username"))
    }

    companion object {
        private const val ARG_USERNAME: String = "username"

        fun newInstance(username: String): SetupFragment {
            val fragment = SetupFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }
}
