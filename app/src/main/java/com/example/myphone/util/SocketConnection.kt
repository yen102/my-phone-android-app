package com.example.myphone.util

object SocketConnection {
    external fun connectToPC(ip: String, port: Int): Int
    external fun sendStringData(data: String, len: Int): Int
    external fun sendByteData(data: ByteArray, len: Int): Int
}