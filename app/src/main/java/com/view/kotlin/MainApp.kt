package com.view.kotlin

import android.app.Application

class MainApp : Application() {
    private var mainApp: MainApp? = null

    override fun onCreate() {
        super.onCreate()
        mainApp = this
    }

    fun getInstance(): MainApp? {
        return mainApp
    }
}