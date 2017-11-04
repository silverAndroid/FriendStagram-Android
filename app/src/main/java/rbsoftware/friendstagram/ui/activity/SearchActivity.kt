package rbsoftware.friendstagram.ui.activity

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.layout_alt_toolbar.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.setTint

class SearchActivity : AppCompatActivity() {

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
        return true
    }
}
