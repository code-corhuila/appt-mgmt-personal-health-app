package com.saludactiva.personalhealth

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.saludactiva.personalhealth.data.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null && SessionManager(this).token != null) {
            val navHost = supportFragmentManager
                .findFragmentById(R.id.nav_host) as NavHostFragment
            navHost.navController.navigate(R.id.profileFragment)
        }
    }
}
