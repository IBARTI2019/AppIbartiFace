package com.oesvica.appibartiFace.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.oesvica.appibartiFace.data.model.auth.LogInRequest

class LogInActivityContract : ActivityResultContract<Unit, LogInRequest?>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        return LoginActivity.starterIntent(context)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): LogInRequest? {
        val user = intent?.getStringExtra(LoginActivity.USER)
        val password = intent?.getStringExtra(LoginActivity.PASSWORD)
        return if (resultCode == Activity.RESULT_OK && user != null && password != null) LogInRequest(
            user,
            password
        )
        else null
    }
}