package com.merttoptas.cizvio.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cizvio.R


class MainFragment : Fragment(), View.OnClickListener {

    var navController: NavController? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=  inflater.inflate(R.layout.fragment_main, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

        view.findViewById<TextView>(R.id.game_rules).setOnClickListener(this)
        view.findViewById<TextView>(R.id.user_agreement).setOnClickListener(this)
        view.findViewById<Button>(R.id.gameCreate).setOnClickListener(this)
        view.findViewById<Button>(R.id.fastGameBtn).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.game_rules -> navController!!.navigate(
                R.id.action_mainFragment_to_gameRulesFragment
            )
            R.id.user_agreement -> navController!!.navigate(
                R.id.action_mainFragment_to_userAgreementFragment
            )
            R.id.gameCreate -> navController!!.navigate(
                R.id.action_mainFragment_to_gameCreateFragment
            )
            R.id.fastGameBtn -> {
                val userName = "merttoptas"
                val bundle = bundleOf("username" to userName)
                navController!!.navigate(R.id.action_mainFragment_to_gameFragment, bundle)
            }

        }
    }

}