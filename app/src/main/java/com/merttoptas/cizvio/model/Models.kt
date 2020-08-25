package com.merttoptas.cizvio.model

data class Message (val userName: String, val messageContent : String, val roomName: String, var viewType : Int)
data class initialData (val userName : String, val roomName : String)
data class SendMessage(val userName : String, val messageContent: String, val roomName: String)
data class SendDraw(val userName : String, val x1: String,  val y1: String, val isDraw: Boolean, val strokeWidth: Float, val colors:Int, val roomName:String)

enum class MessageType(val index : Int){
    CHAT_MINE(0),CHAT_PARTNER(1);
}

enum class DrawType(val index : Int){
    DRAW_MINE(0),DRAW_PARTNER(1);
}