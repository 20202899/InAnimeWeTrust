package developer.carlos.silva.activities

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import developer.carlos.silva.R
import developer.carlos.silva.fragments.FavoriteFragment

import kotlinx.android.synthetic.main.activity_favorite.*

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setSupportActionBar(toolbar)

        title = "Favoritos"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.container, FavoriteFragment())
            .commit()


        setResult(MainActivity.REQUEST_RESULT)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home)
            finishAfterTransition()

        return super.onOptionsItemSelected(item)
    }

}
