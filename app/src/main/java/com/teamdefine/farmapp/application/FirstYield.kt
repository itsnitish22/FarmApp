package com.teamdefine.farmapp.application

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import io.socket.client.IO
import io.socket.client.Socket

class FirstYield : Application() {
    var socketIO: Socket? = IO.socket("http://192.168.2.18:3000/")

    override fun onCreate() {
        super.onCreate()
        try {
            socketIO?.connect()
            Log.i("Application Socket Try", "Socket Try Connection")
        } catch (e: Exception) {
            Log.e("Application Socket Error", e.toString())
        }
        FirebaseApp.initializeApp(this)
    }
}