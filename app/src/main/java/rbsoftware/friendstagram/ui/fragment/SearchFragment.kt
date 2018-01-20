package rbsoftware.friendstagram.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_search.*
import rbsoftware.friendstagram.InitializerApp
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.onError
import rbsoftware.friendstagram.ui.adapter.SearchAdapter
import rbsoftware.friendstagram.viewmodel.SearchViewModel
import retrofit2.HttpException
import javax.inject.Inject

/**
 * A placeholder fragment containing a simple view.
 */
class SearchFragment : Fragment() {
    @Inject
    lateinit var searchViewModel: SearchViewModel
    lateinit var query: String
    var searchDisposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val daggerComponent = InitializerApp.servicesComponent
        daggerComponent.inject(this)

        searchViewModel = daggerComponent.searchViewModel()
        if (arguments != null) {
            query = arguments.getString(ARG_QUERY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (query.isNotBlank()) {
            searchDisposable = searchViewModel.searchUsers(query)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ response ->
                        if (response.isSuccessful) {
                            val users = response.body()?.data!!
                            recyclerView.adapter = SearchAdapter(users)
                            recyclerView.layoutManager = LinearLayoutManager(context)
                        } else {
                            onError(SearchFragment::class.java, context, HttpException(response), "Failed to load search results")
                        }
                        loading.visibility = View.GONE
                    }, { onError(SearchFragment::class.java, context, it) })
        } else {
            loading.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()

        searchDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

    companion object {
        private val ARG_QUERY = "query"

        fun newInstance(query: String): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()

            args.putString(ARG_QUERY, query)
            fragment.arguments = args
            return fragment
        }
    }
}
