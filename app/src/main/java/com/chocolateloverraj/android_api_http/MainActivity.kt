package com.chocolateloverraj.android_api_http

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import com.chocolateloverraj.android_api_http.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var serviceIntent: Intent
    lateinit var toggleButton: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        serviceIntent = Intent(this, HttpServerService::class.java)
        toggleButton = findViewById(R.id.toggleButton)
        toggleButton.isChecked = HttpServerService.started
    }

    fun onToggle(view: View) {
        if (toggleButton.isChecked) {
            if (!HttpServerService.started) {
                startForegroundService(serviceIntent)
            }
        } else {
            Log.d(MainActivity::class.java.name, "stopping service")
            Log.d(MainActivity::class.java.name, stopService(serviceIntent).toString())
        }
    }

}