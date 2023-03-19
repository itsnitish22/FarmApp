package com.teamdefine.farmapp.application

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import io.socket.client.IO
import io.socket.client.Socket

class FirstYield : Application() {
    var socketIO: Socket? = IO.socket("http://10.10.4.41/")

    override fun onCreate() {
        super.onCreate()
        try {
            Log.i("Application Socket Success", "Socket Connected")
            socketIO?.connect()
        } catch (e: Exception) {
            Log.e("Application Socket Error", e.toString())
        }
        FirebaseApp.initializeApp(this)
    }
}