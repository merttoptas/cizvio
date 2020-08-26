package com.merttoptas.cizvio.ui.entrance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.ui.serverDrawView.ServerDrawView
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.android.synthetic.main.fragment_game.*

class EntranceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entrance)
    }
}