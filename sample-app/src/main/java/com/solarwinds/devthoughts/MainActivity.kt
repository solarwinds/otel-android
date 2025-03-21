package com.solarwinds.devthoughts

import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.solarwinds.devthoughts.data.AppViewModel
import com.solarwinds.devthoughts.data.Dev
import com.solarwinds.devthoughts.data.DevThoughtsDatabase
import com.solarwinds.devthoughts.data.Repository
import com.solarwinds.devthoughts.data.Thought
import com.solarwinds.devthoughts.databinding.ActivityMainBinding
import com.solarwinds.devthoughts.ui.onboarding.sessionIdPreferenceKey
import com.solarwinds.devthoughts.utils.dataStore
import com.solarwinds.devthoughts.utils.meterProviderName
import com.solarwinds.devthoughts.utils.solarwindsRum
import io.opentelemetry.api.common.AttributeKey
import io.opentelemetry.api.common.Attributes
import io.opentelemetry.api.metrics.LongCounter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private lateinit var repository: Repository

    private val appViewModel: AppViewModel by viewModels()

    var dev: Dev? = null

    private var sessionId: String = "unset"

    private lateinit var thoughtCounter: LongCounter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = Repository.create(DevThoughtsDatabase.getInstance(applicationContext))
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.appBarMain.fab.setOnClickListener { view ->
            showInputDialog(view)
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lifecycleScope.launch {
            repository.findAllDev().collect {
                if (it.isNotEmpty()) {
                    val lastDev = it.last()
                    val layout = binding.navView.getHeaderView(0)
                    val otherInfo: TextView = layout.findViewById(R.id.otherInfo)
                    val username: TextView = layout.findViewById(R.id.username)

                    val formatedInfo = "${lastDev.favoriteLang} | ${lastDev.favoriteIde}"
                    otherInfo.text = formatedInfo
                    username.text = lastDev.username

                    dev = lastDev
                    appViewModel.updateDev(lastDev)
                }
            }
        }

        lifecycleScope.launch {
            dataStore.data.map { settings ->
                settings[sessionIdPreferenceKey] ?: "unset"
            }.collectLatest {
                sessionId = it
            }
        }

        thoughtCounter = this.solarwindsRum()
            .meter(this.meterProviderName)
            .counterBuilder("thought.count")
            .build()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showInputDialog(view: View) {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("What's on your mind?")
            .setView(editText)
            .setCancelable(true)
            .setPositiveButton("Submit") { dialog, _ ->
                val thought = editText.text.toString()
                var msg = "Your thought has been registered"
                if (dev != null) {
                    repository.writeThought(Thought(0, dev!!.devId, thought))
                    thoughtCounter.add(
                        1, Attributes.of(
                            AttributeKey.stringKey("username"), dev!!.username!!,
                            AttributeKey.stringKey("session.id"), sessionId,
                        )
                    )
                } else {
                    msg = "Your thought has not been registered"
                }

                dialog.cancel()
                Snackbar.make(
                    view,
                    msg,
                    Snackbar.LENGTH_LONG
                ).setAnchorView(R.id.fab)
                    .show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}