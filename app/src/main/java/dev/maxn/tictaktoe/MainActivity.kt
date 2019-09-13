package dev.maxn.tictaktoe

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel

    private val prefs: SharedPreferences by lazy {
        val ctx = App.applicationContext()
        PreferenceManager.getDefaultSharedPreferences(ctx)
    }
    private var cells: List<ImageView> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewModel()
        initViews()
        if (viewModel.getFirstMove() == "human") viewModel.humanMove() else viewModel.robotMove()
    }

    private fun initViews() {
        cells = listOf(cell_0, cell_1, cell_2, cell_3, cell_4, cell_5, cell_6, cell_7, cell_8)
        updateUi()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getState().observe(this, Observer { updateUi() })

    }

    private fun updateUi() {
        for ((i, x) in viewModel.getBoard().withIndex()) {
            when (x.state) {
                "" -> {
                    cells[i].setOnClickListener {
                        cells[i].setBackgroundResource(viewModel.getDrawable())
                        viewModel.move(i)
                    }
                    cells[i].setBackgroundResource(R.drawable.empty)
                }
                "x" -> {
                    cells[i].isClickable = false
                    cells[i].setBackgroundResource(R.drawable.x)
                }
                "0" -> {
                    cells[i].isClickable = false
                    cells[i].setBackgroundResource(R.drawable.o)
                }
            }
        }
        if (viewModel.isClosed()) {
            val message = when {
                viewModel.isWinner() -> "You win!"
                else -> "You lose!"
            }
            Toast.makeText(App.applicationContext(), message, Toast.LENGTH_LONG).show()
            for ((i, x) in viewModel.getBoard().withIndex()) {
                cells[i].isClickable = false
            }
        } else {
            if (viewModel.isFull()) {
                Toast.makeText(App.applicationContext(), "Draw", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menuNew -> {
                viewModel.initNewGame()
                updateUi()
                true
            }
            R.id.menuSettings -> {
                settingsCall()
                true
            }
            R.id.menuExit -> {
                System.exit(0)
                true
            }
            else -> false
        }
    }

    private fun settingsCall() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }


}