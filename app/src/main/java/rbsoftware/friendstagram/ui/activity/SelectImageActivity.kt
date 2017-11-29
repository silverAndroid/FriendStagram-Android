package rbsoftware.friendstagram.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.layout_alt_toolbar.*
import rbsoftware.friendstagram.R
import rbsoftware.friendstagram.setTint
import rbsoftware.friendstagram.ui.fragment.SelectImageFragment

class SelectImageActivity : AppCompatActivity() {
    private val uiSubscriptions: CompositeDisposable = CompositeDisposable()

    private var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_image)

        val closeIndicator = ContextCompat.getDrawable(this, R.drawable.ic_close)
        setSupportActionBar(toolbar)
        DrawableCompat.setTint(closeIndicator, Color.WHITE)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(closeIndicator)

        val fragment = supportFragmentManager.findFragmentById(R.id.fragment_select_image) as SelectImageFragment
        uiSubscriptions.add(
                fragment.isAdapterInitialized()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.trampoline())
                        .subscribe({
                            fragment.getOnImageSelected()?.let { subject ->
                                uiSubscriptions.add(
                                        subject.observeOn(AndroidSchedulers.mainThread())
                                                .subscribeOn(Schedulers.trampoline())
                                                .subscribe({ uri ->
                                                    imageURI = uri
                                                    invalidateOptionsMenu()
                                                }, { e ->
                                                    Log.e(TAG, getString(R.string.error_occurred), e)
                                                })
                                )
                            }
                        }, this::onSubscriptionError)
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_select_image, menu)
        val checkMenuItem = menu.findItem(R.id.item_select)
        checkMenuItem.setTint(this, menu, R.color.colorWhite)
        return imageURI != null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onCancelled()
                true
            }
            R.id.item_select -> {
                onImageSelected()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onImageSelected() {
        val returnIntent = Intent()
        returnIntent.putExtra(URI_RESULT_KEY, imageURI)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun onCancelled() {
        onBackPressed()
    }

    private fun onSubscriptionError(error: Throwable): Unit = throw RuntimeException(error)

    companion object {
        private const val TAG = "SelectImageActivity"

        const val URI_RESULT_KEY = "image_uri"
    }
}
