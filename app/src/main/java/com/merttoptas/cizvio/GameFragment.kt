package com.merttoptas.cizvio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.merttoptas.cizvio.ui.clientDrawView.ClientDrawView
import com.merttoptas.cizvio.ui.serverDrawView.ServerDrawView
import kotlinx.android.synthetic.main.fragment_game.*

class GameFragment : Fragment() {
    lateinit var clientDrawView: ServerDrawView
    lateinit var serverDrawView: ClientDrawView

    val drawOwner = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_game, container, false)

       // clientDrawView = view.findViewById(R.id.clientDrawView)
        serverDrawView = view.findViewById(R.id.serverDrawView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}