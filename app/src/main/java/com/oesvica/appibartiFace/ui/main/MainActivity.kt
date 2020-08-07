package com.oesvica.appibartiFace.ui.main

import android.annotation.TargetApi
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.launch
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.oesvica.appibartiFace.CHANNEL_ID
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import com.oesvica.appibartiFace.utils.isOreoOrLater
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : DaggerActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }
    private var appBarConfiguration: AppBarConfiguration? = null
    private val headerView by lazy { navView.getHeaderView(0) }
    private val logInButton by lazy { headerView.findViewById<Button>(R.id.logInButton) }
    private val progressBar by lazy { headerView.findViewById<ProgressBar>(R.id.progressBar) }
    private val username by lazy { headerView.findViewById<TextView>(R.id.username) }
    private val mainViewModel: MainViewModel by viewModels { viewModelFactory }
    private val openLoginActivity = registerForActivityResult(LogInActivityContract()) { result ->
        result?.let {
            mainViewModel.logIn(it.usuario, it.clave)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        if (isOreoOrLater()) createNotificationChannel()
        observeAuth()
        observeSnackbarMessages()
        mainViewModel.loadAuthInfo()
        checkForGooglePlayServices() // check because of FirebaseCloudMessaging needing that sdk
    }

    /**
     * Verify Google Play Services is available and in case it is not show an ErrorDialog
     */
    private fun checkForGooglePlayServices(){
        val status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        // Showing status
        if (status != ConnectionResult.SUCCESS) {
            Snackbar.make(drawerLayout, "Google Play Services no esta disponible", Snackbar.LENGTH_LONG)
            val requestCode = 10
            val dialog: Dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, status, requestCode)
            dialog.show()
        }
    }

    private fun observeAuth() {
        mainViewModel.authInfo.observe(this) {
            debug("authInfo = $it")
            if (it.isLoading) {
                progressBar.visibility = View.VISIBLE
                logInButton.visibility = View.GONE
                username.visibility = View.GONE
                nav_host_fragment.view?.visibility = View.GONE
            } else if (it.success?.logIn == true) {
                logInButton.text = "Cerrar sesion"
                progressBar.visibility = View.GONE
                logInButton.visibility = View.VISIBLE
                username.visibility = View.VISIBLE
                logInButton.setOnClickListener {
                    navController.navigateUp()
                    mainViewModel.logOut()
                }
                nav_host_fragment.view?.visibility = View.VISIBLE
                mainViewModel.getUsername()?.let { usr -> username.text = usr }
            } else {
                logInButton.text = "Iniciar sesion"
                progressBar.visibility = View.GONE
                logInButton.visibility = View.VISIBLE
                username.visibility = View.GONE
                logInButton.setOnClickListener {
                    openLoginActivity.launch()
                }
                nav_host_fragment.view?.visibility = View.GONE
                drawerLayout.openDrawer(navView)
            }
            setUpNavView()
            setUpNavMenuVisibility()
        }
    }

    private fun observeSnackbarMessages() {
        mainViewModel.snackBarMsg.observe(this) {
            it?.let {
                debug("snack $it")
                Snackbar.make(drawerLayout, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpNavMenuVisibility() {
        navView.menu.forEach {
            if (it.hasSubMenu()) {
                it.subMenu.forEach { it.isVisible = false }
            } else {
                it.isVisible = false
            }
        }
        mainViewModel.getMenusToDisplayForUser()?.forEach {
            navView.menu.findItem(it)?.isVisible = true
        }
    }

    private fun setUpNavView() {
        val menusToDisplayForUser = mainViewModel.getMenusToDisplayForUser() ?: return
        if (!menusToDisplayForUser.contains(R.id.nav_standby) && navController.graph.startDestination == R.id.nav_standby) {
            debug("getting rid of standby")
            val graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
            graph.startDestination = menusToDisplayForUser.first()
            navController.graph = graph
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            menusToDisplayForUser.toSet(),
            drawerLayout
        )
        appBarConfiguration?.let { setupActionBarWithNavController(navController, it) }
        navView.setupWithNavController(navController)

        ActionBarDrawerToggle(
            this@MainActivity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ).apply {
            drawerLayout.addDrawerListener(this)
            syncState()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return appBarConfiguration?.let { navController.navigateUp(it) } == true || super.onSupportNavigateUp()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        // Create the NotificationChannel
        val name = "IbartiAppChannel"
        val descriptionText = "Notifications from Ibarti App"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
        mChannel.importance = NotificationManager.IMPORTANCE_HIGH
        mChannel.description = descriptionText
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        val notificationManager =
            getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}
