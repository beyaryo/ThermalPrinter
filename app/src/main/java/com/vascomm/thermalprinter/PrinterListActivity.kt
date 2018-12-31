package com.vascomm.thermalprinter

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.lynx.wind.recycleradapter.RecyclerAdapter
import kotlinx.android.synthetic.main.activity_printer_list.*
import kotlinx.android.synthetic.main.item_bluetooth.view.*

class PrinterListActivity : AppCompatActivity() {

    private val adapter = BluetoothAdapter.getDefaultAdapter()
    private val devices = ArrayList<BluetoothDevice>()
    private var device: BluetoothDevice? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val data = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (devices.indexOf(data) == -1) {
                        devices.add(data)
                        list.adapter.notifyDataSetChanged()
                    }
                }
                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)

                    if (state == BluetoothDevice.BOND_BONDED) setResult()
                }
            }
        }
    }

    private val REQ_ENABLE_BT = 8731

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_printer_list)

        registerReceiver(receiver, IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        })

        setupView()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQ_ENABLE_BT) adapter.startDiscovery()
    }

    private fun setupView() {
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list.adapter = object : RecyclerAdapter<BluetoothHolder, BluetoothDevice>(
                BluetoothHolder::class.java, devices, R.layout.item_bluetooth) {
            override fun onBind(holder: BluetoothHolder?, data: BluetoothDevice?, index: Int) {
                holder?.bind(data)
                holder?.itemView?.setOnClickListener {
                    device = data

                    if (data?.uuids == null) data?.createBond()
                    else setResult()
                }
            }
        }

        if (adapter.isDiscovering) adapter.cancelDiscovery()

        startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQ_ENABLE_BT)
    }

    private fun setResult() {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra("data", device) })
        finish()
    }
}

class BluetoothHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(data: BluetoothDevice?) {
        itemView.txt_name.text = data?.name
        itemView.txt_id.text = data?.address
    }
}