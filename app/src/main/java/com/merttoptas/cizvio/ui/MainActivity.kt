package com.merttoptas.cizvio.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.ui.clientDrawView.ClientDrawView
import com.merttoptas.cizvio.ui.serverDrawView.ServerDrawView

class MainActivity : AppCompatActivity() {
    private  var serverDrawView: ServerDrawView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}