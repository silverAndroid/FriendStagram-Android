package rbsoftware.friendstagram.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import kotlinx.android.synthetic.main.fragment_edit_profile.*
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.model.User
import rbsoftware.friendstagram.onError
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.ui.adapter.EditProfileAdapter
import rbsoftware.friendstagram.viewmodel.UserViewModel
import retrofit2.HttpException

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileFragment : Fragment() {
    private val updateComplete: CompletableSubject = CompletableSubject.create()

    private lateinit var user: User
    private lateinit var adapter: EditProfileAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments.getParcelable(ARG_USER)
        adapter = EditProfileAdapter(user)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_edit_profile, container, false)
    }

    @SuppressLint("RxLeakedSubscription")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = rv
        val btnSubmit = submit

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)
        authService = daggerComponent.authService()
        userViewModel = daggerComponent.userViewModel()

        recyclerView?.adapter = adapter
        recyclerView?.layoutManager = LinearLayoutManager(context)
        btnSubmit?.setOnClickListener {
            val adapterData = adapter.getData()
            adapterData?.let { data ->
                userViewModel.updateUser(authService.username, data)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ response ->
                            if (response.isSuccessful) {
                                val message = response.body()?.data
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                updateComplete.onComplete()
                            } else {
                                onError(TAG, context, HttpException(response))
                            }
                        }, { onError(TAG, context, it, "Failed to update user") })
            }

        }
    }

    fun updateComplete() = updateComplete

    companion object {
        private const val TAG = "EditProfileFragment"
        private const val ARG_USER = "user"

        fun newInstance(user: User): EditProfileFragment {
            val fragment = EditProfileFragment()
            val args = Bundle()
            args.putParcelable(ARG_USER, user)
            fragment.arguments = args
            return fragment
        }
    }
}