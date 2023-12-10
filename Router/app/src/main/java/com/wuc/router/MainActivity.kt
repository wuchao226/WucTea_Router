package com.wuc.router

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wuc.router.annotation.Destination

@Destination(url = "router://page-home", description = "应用主页")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}