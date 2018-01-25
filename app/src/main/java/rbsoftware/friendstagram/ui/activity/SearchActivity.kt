package rbsoftware.friendstagram.ui.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_alt_toolbar.*
import rbsoftware.friendstagram.*
import rbsoftware.friendstagram.ui.fragment.SearchFragment
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {
    private var query: String? = null
    private lateinit var searchListenerDisposable: Disposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        searchMenuItem.setTint(this, menu, R.color.colorWhite)
        searchMenuItem.expandActionView()
        searchMenuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean = true

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                onSupportNavigateUp()
                return false
            }
        })

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchMenuItem.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchListenerDisposable = searchView.listenForQueries()
                .debounce(200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.trampoline())
                .subscribe({
                    showFragment(SearchFragment.newInstance(it))
                    query = it
                }, { onError(SearchActivity::class.java, this, it) })
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!searchListenerDisposable.isDisposed) {
            searchListenerDisposable.dispose()
        }
    }
}
