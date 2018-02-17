package com.example.surendar_5541.ssbbanksystemdemoapplication

import android.app.Application
import com.facebook.stetho.Stetho
import com.idescout.sql.SqlScoutServer

/**
 * Created by surendar-5541 on 14/11/17.
 */


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        SqlScoutServer.create(this, packageName);
    }

}