package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.ui.adapter.EditProfileAdapter

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView? = view?.findViewById(R.id.rv)
        val adapter = EditProfileAdapter()

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
    }

    companion object {
        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }
}