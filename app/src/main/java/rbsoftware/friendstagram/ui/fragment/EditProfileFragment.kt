package rbsoftware.friendstagram.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.dagger.component.DaggerServicesComponent
import rbsoftware.friendstagram.dagger.module.AppModule
import rbsoftware.friendstagram.dagger.module.ServicesModule
import rbsoftware.friendstagram.service.AuthenticationService
import rbsoftware.friendstagram.service.NetworkService
import rbsoftware.friendstagram.ui.adapter.EditProfileAdapter
import rbsoftware.friendstagram.viewmodel.UserViewModel

/**
 * Created by Rushil on 8/23/2017.
 */
class EditProfileFragment : Fragment() {
    private val adapter = EditProfileAdapter()
    private val updateComplete: CompletableSubject = CompletableSubject.create()

    private lateinit var userViewModel: UserViewModel
    private lateinit var authService: AuthenticationService

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_edit_profile, container, false)
    }

    @SuppressLint("RxLeakedSubscription")
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView? = view?.findViewById(R.id.rv)
        val btnSubmit: Button? = view?.findViewById(R.id.submit)

        val daggerComponent = DaggerServicesComponent
                .builder()
                .appModule(AppModule(context))
                .servicesModule(ServicesModule())
                .build()
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
                                val error = NetworkService.parseError(response.errorBody())
                                handleError(error)
                            }
                        }, this::onNetworkError)
            }

        }
    }

    fun updateComplete() = updateComplete

    private fun handleError(error: String?) {
        when (error) {
            null -> Toast.makeText(context, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            else -> Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun onNetworkError(error: Throwable) {
        Log.e(TAG, "Network error", error)
        Toast.makeText(context, getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "EditProfileFragment"

        fun newInstance(): EditProfileFragment {
            return EditProfileFragment()
        }
    }
}