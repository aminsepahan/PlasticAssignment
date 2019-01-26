package com.amin.plasticassignment.utils

import android.app.Application
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class App : Application() {


    private var mRequestQueue: RequestQueue? = null
        get() = if (field == null) {
            field = Volley.newRequestQueue(applicationContext)
            field
        } else field

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    fun <T> addToRequestQueue(req: com.android.volley.Request<T>) {
        mRequestQueue?.add(req)
    }

    companion object {
        lateinit var app: App
    }
}