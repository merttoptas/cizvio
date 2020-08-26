package com.merttoptas.cizvio.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.ui.clientDrawView.ClientDrawView
import com.merttoptas.cizvio.ui.serverDrawView.ServerDrawView
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game_create.*


class GameCreateFragment : Fragment(), View.OnClickListener {
    var navController: NavController? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_game_create, container, false)

        return  view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        view.findViewById<Button>(R.id.gameCreateButton).setOnClickListener(this)
        view.findViewById<Button>(R.id.gameCreateBackButton).setOnClickListener(this)

        initSpinner()
    }

    private fun initSpinner() {
        var category =
            arrayOf("Genel", "Hayvanlar", "Şehir", "Eşya")


        val categoryAdapter = spinnerAdapter()
        val roomNumberAdapter = spinnerAdapter()

        for (i in 0..category.size - 1) {
            categoryAdapter.add(category[i])
        }
        categoryAdapter.add("Kategori Seçimi")
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        for (i in 0..10) {
            roomNumberAdapter.add("$i")
        }
        roomNumberAdapter.add("Oyuncu Sayısı Seçimi")
        roomNumberAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
        gameRoomPlayerNumberSpinner.adapter = roomNumberAdapter
        gameRoomCategorySpinner.adapter = categoryAdapter
        gameRoomPlayerNumberSpinner.setSelection(roomNumberAdapter.count)
        gameRoomCategorySpinner.setSelection(categoryAdapter.count)
    }

    private fun spinnerAdapter(): ArrayAdapter<String?> {
        return object : ArrayAdapter<String?>(requireContext(), R.layout.spinner_text) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                val text = (v.findViewById<View>(android.R.id.text1) as TextView)
                if (position == count) {
                    text.text = ""
                    text.hint = getItem(count) //"Hint to be displayed"
                }
                return v
            }

            override fun getCount(): Int {
                return super.getCount() - 1 // you dont display last item. It is used as hint.
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id) {
            R.id.gameCreateButton -> navController!!.navigate(
                R.id.action_gameCreateFragment_to_gameFragment
            )

            R.id.gameCreateBackButton -> navController!!.navigate(
                R.id.action_gameCreateFragment_to_mainFragment
            )
        }

    }
}