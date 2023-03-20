package com.teamdefine.farmapp

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.teamdefine.farmapp.application.FirstYield
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {
    var socketIOInstance: Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}