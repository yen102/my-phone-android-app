package com.example.myphone.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset


class ImageHandler(context: Context) {
    private val imageData = arrayListOf<ByteArray>()
    init {
        Log.d("ImageData", "init")
        getThumbNails(context.contentResolver)
    }
    fun sendImages() {
        if (imageData.isEmpty()) return
        Log.d("ImageData", imageData[0].size.toString())
        val base64Str = Base64.encodeToString(imageData[0], Base64.NO_PADDING)
//        Log.d("ImageData", base64Str)
        SocketConnection.sendByteData(imageData[0], imageData[0].size)
    }
    private fun getThumbNails(contentResolver: ContentResolver) {
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            "date_modified DESC"
        )
            ?: return
        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                val bitmap = MediaStore.Images.Thumbnails.getThumbnail(contentResolver, cursor.getString(0).toLong(),MediaStore.Images.Thumbnails.MICRO_KIND, null)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val byteArray = stream.toByteArray()
                val id = cursor.getString(0)
                val title = cursor.getString(5)

                val idBytes = id.toByteArray(Charset.defaultCharset()).copyOf(8)
                val titleBytes = title.toByteArray(Charset.defaultCharset()).copyOf(256)
                val len = (byteArray.size + 8 + 256 + 8).toString().toByteArray(Charset.defaultCharset()).copyOf(8)

                val sendBytes = len + idBytes + titleBytes + byteArray
                imageData.add(sendBytes)
                break
            } while (cursor.moveToNext())
        } else {
            // no image
        }
        cursor.close()
    }
}