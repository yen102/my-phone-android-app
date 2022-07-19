package com.example.myphone.util

object QRHandler {
    fun extractQRText(qrText: String): Pair<String, Int> {
        val extracted = qrText.split(":")
        return Pair(extracted[0], extracted[1].toInt())
    }
}