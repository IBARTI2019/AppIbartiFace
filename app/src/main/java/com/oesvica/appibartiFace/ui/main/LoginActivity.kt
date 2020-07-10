package com.oesvica.appibartiFace.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.oesvica.appibartiFace.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    companion object {

        const val USER = "user"
        const val PASSWORD = "password"

        fun starterIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        logInButton.setOnClickListener {
            val user = userEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (user.isEmpty()) {
                Toast.makeText(
                    this,
                    "Debe ingresar un valor valido en el campo usuario",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Debe ingresar un valor valido en el campo clave",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(USER, user)
                putExtra(PASSWORD, password)
            })
            finish()
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}