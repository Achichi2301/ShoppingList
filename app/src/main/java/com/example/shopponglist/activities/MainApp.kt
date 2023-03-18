package com.example.shopponglist.activities

import android.app.Application
import com.example.shopponglist.db.MainDataBase

class MainApp : Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}