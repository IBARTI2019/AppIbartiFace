package com.oesvica.appibartiFace.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.result.launch
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.oesvica.appibartiFace.R
import com.oesvica.appibartiFace.utils.base.DaggerActivity
import com.oesvica.appibartiFace.utils.debug
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : DaggerActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private val headerView by lazy { navView.getHeaderView(0) }
    private val logInButton by lazy { headerView.findViewById<Button>(R.id.logInButton) }
    private val progressBar by lazy { headerView.findViewById<ProgressBar>(R.id.progressBar) }
    private val mainViewModel by lazy { getViewModel<MainViewModel>() }
    private val openLoginActivity = registerForActivityResult(LogInActivityContract()) { result ->
        result?.let {
            mainViewModel.logIn(it.usuario, it.clave)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setUpNavView()
        mainViewModel.authInfo.observe(this, Observer {
            debug("authInfo = $it")
            if (it.isLoading) {
                progressBar.visibility = View.VISIBLE
                logInButton.visibility = View.GONE
            } else if (it.success?.logIn == true) {
                logInButton.text = "Cerrar sesion"
                progressBar.visibility = View.GONE
                logInButton.visibility = View.VISIBLE
                logInButton.setOnClickListener {
                    mainViewModel.logOut()
                }
            } else if (it.success?.logIn == false){
                logInButton.text = "Iniciar sesion"
                progressBar.visibility = View.GONE
                logInButton.visibility = View.VISIBLE
                logInButton.setOnClickListener {
                    openLoginActivity.launch()
                }
            }
        })
        mainViewModel.loadAuthInfo()
    }

    private fun setUpNavView() {
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_standby,
                R.id.nav_categorias,
                R.id.nav_status,
                R.id.nav_personas,
                R.id.nav_asistencia_ibarti
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
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
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
