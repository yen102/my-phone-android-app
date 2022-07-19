package com.example.myphone

import android.content.pm.PackageManager
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.myphone.databinding.ActivityMainBinding
import com.example.myphone.util.ImageHandler
import com.example.myphone.util.QRHandler.extractQRText
import com.example.myphone.util.SMSMessagesHandler
import com.example.myphone.util.SocketConnection

class MainActivity : AppCompatActivity() {
    private lateinit var scanner: CodeScanner
    private lateinit var binding: ActivityMainBinding
    private lateinit var smsMessagesHandler: SMSMessagesHandler
    private val imageHandler:  ImageHandler by lazy {
        ImageHandler(this)
    }

    private val permissionList = arrayListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        smsMessagesHandler = SMSMessagesHandler(this)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpScanner()
        setUpViews()
        setUpPermissions()
    }

    private fun setUpViews() {
        binding.btnSend.setOnClickListener {
            imageHandler.sendImages()
        }
    }

    private fun setUpPermissions() {
        val permissionCamera = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        )

        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.CAMERA)
        }

        val permissionReadSMS = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.READ_SMS
        )

        if (permissionReadSMS != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_SMS)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.RECEIVE_SMS)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(android.Manifest.permission.SEND_SMS)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionList.isNotEmpty()) {
            makeRequests(permissionList)
        }
    }

    private fun makeRequests(permissionList: ArrayList<String>) {
        ActivityCompat.requestPermissions(
            this,
            permissionList.toTypedArray(),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                Log.d("Length", permissions.size.toString())
            }
        }
    }

    private fun setUpScanner() {
        val scannerView = binding.scannerView
        scanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        scanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        scanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        scanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        scanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        scanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        scanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        scanner.decodeCallback = DecodeCallback {
            val connectRes = connectWithQR(it.text)
            if (connectRes > 0) {
//                smsMessagesHandler.sendSMSHistory()
//                imageHandler.sendImages()
            }
        }
        scanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            scanner.startPreview()
        }
    }

    private fun connectWithQR(qrText: String): Int {
        val (ip, port) = extractQRText(qrText)
        val res = SocketConnection.connectToPC(ip, port)
        if (res < 0) {
            runOnUiThread {
                Toast.makeText(this, "Error in trying to connect with PC!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "Connected to PC", Toast.LENGTH_SHORT).show()
            }
        }
        return res
    }

    override fun onResume() {
        super.onResume()
        scanner.startPreview()
    }

    override fun onPause() {
        scanner.releaseResources()
        super.onPause()
    }

    companion object {
        init {
            System.loadLibrary("myphone")
        }

        const val PERMISSION_REQUEST_CODE = 100
        const val TAG = "MainActivity"

    }

}