package com.vascomm.thermalprinter

import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class MainActivity : AppCompatActivity() {

    private var socket: BluetoothSocket? = null
    private var device: BluetoothDevice? = null

    private var outputStream: OutputStream? = null
    private lateinit var inputStream: InputStream

    private lateinit var readBuffer: Array<Byte>
    private var readBufferIndex = 0

    private val REQ_DEVICE_BT = 7831

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_dummy.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, PrinterListActivity::class.java), REQ_DEVICE_BT)
        }

        btn_print.setOnClickListener { print() }
    }

    override fun onPause() {
        super.onPause()
        socket?.close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == REQ_DEVICE_BT){
            device = data?.extras?.getParcelable("data")
            connectBt()
        }
    }

    private fun connectBt(){
        launch {
            socket = device?.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))
            socket?.connect()

            outputStream = socket?.outputStream
            outputStream?.flush()

            runOnUiThread { toast("Bluetooth Connected") }
        }
    }

    private fun print(){
        Printer(outputStream).dummy().print()
    }

    private fun Context.toast(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
}
