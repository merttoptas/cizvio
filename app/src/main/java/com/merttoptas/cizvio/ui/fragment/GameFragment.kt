package com.merttoptas.cizvio.ui.fragment

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.merttoptas.cizvio.R
import com.merttoptas.cizvio.model.Message
import com.merttoptas.cizvio.model.MessageType
import com.merttoptas.cizvio.model.SendMessage
import com.merttoptas.cizvio.model.initialData
import com.merttoptas.cizvio.ui.adapter.ChatAdapter
import com.merttoptas.cizvio.ui.clientDrawView.ClientDrawView
import com.merttoptas.cizvio.ui.serverDrawView.ServerDrawView
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_game.*


class GameFragment : Fragment(R.layout.fragment_game), View.OnClickListener {
    private lateinit var edittext :EditText
    lateinit var mSocket: Socket
    private var serverDrawView: ServerDrawView?=null
    private var clientDrawView: ClientDrawView? = null
    val chatList: ArrayList<Message> = arrayListOf();
    lateinit var chatRoomAdapter: ChatAdapter
    var userName : String = ""
    val gson: Gson = Gson()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        serverLayout.layoutParams =
            FrameLayout.LayoutParams(displayMetrics.widthPixels, displayMetrics.widthPixels)
        serverDrawView = view.findViewById(R.id.serverDrawViewLayout)
        clientDrawView = view.findViewById(R.id.clientDrawViewLayout)
        edittext = view.findViewById(R.id.edittext)
        chatRoomAdapter =
            ChatAdapter(activity!!, chatList)
        recyclerView.adapter = chatRoomAdapter

        val layoutManager = LinearLayoutManager(activity!!)
        recyclerView.layoutManager = layoutManager

        try {
            mSocket = IO.socket("https://cryptic-castle-13142.herokuapp.com")
            Log.d("success", "Connnection")

        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("fail", "Failed to connect")
        }

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on("newMessage", onUpdateChat)
        mSocket.on("updateChat", onUpdateChat)
        mSocket.on("newUserToChatRoom", onNewUser)

        userName = arguments?.getString("username")!!
        Log.d("deneme", userName)

        if (userName.equals("merttoptas")) {
            serverDrawViewLayout.visibility = View.GONE
            clientDrawViewLayout.visibility = View.VISIBLE
            clientDrawView?.createSocketServer()

        } else {
            serverDrawView?.createSocketServer()

        }

        val send = view.findViewById<Button>(R.id.send)
        send.setOnClickListener(this)
    }
    var onConnect = Emitter.Listener {
        val data = initialData("b", "a")
        val jsonData = gson.toJson(data)
        mSocket.emit("subscribe", jsonData)

    }

    var onNewUser = Emitter.Listener {
        val name = it[0] as String //This pass the userName!
        val chat = Message(name, "geldi", "a", MessageType.CHAT_PARTNER.index)
          addItemToRecyclerView(chat)
    }
    var onUpdateChat = Emitter.Listener {
        val chat: Message = gson.fromJson(it[0].toString(), Message::class.java)
        chat.viewType = MessageType.CHAT_PARTNER.index
        addItemToRecyclerView(chat)
    }

     fun addItemToRecyclerView(message: Message) {
         activity!!.runOnUiThread {
             chatList.add(message)
             edittext.setText("")
             recyclerView.scrollToPosition(chatList.size - 1)
         }
    }


    override fun onDetach() {
        super.onDetach()

    }

    fun sendMessage() {
        val content = edittext.text.toString()
        val sendData = SendMessage("b",content,"a")
        val jsonData = gson.toJson(sendData)
        mSocket.emit("newMessage", jsonData)
        val message = Message("b",content,"a", MessageType.CHAT_MINE.index)
        addItemToRecyclerView(message)
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.send -> sendMessage()
        }
    }
}

