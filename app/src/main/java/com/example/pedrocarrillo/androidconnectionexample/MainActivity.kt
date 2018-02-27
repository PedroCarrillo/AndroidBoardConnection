package com.example.pedrocarrillo.androidconnectionexample

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.pedrocarrillo.androidconnectionexample.data.Board
import com.example.pedrocarrillo.androidconnectionexample.socket.SocketService
import android.support.v4.content.LocalBroadcastManager
import android.content.IntentFilter
import android.util.Log
import com.example.pedrocarrillo.androidconnectionexample.data.Message


class MainActivity : AppCompatActivity(), SocketBroadcastListener {

    lateinit var btnStartSocketConnection : Button
    lateinit var btnEndSocketConnection : Button
    lateinit var tvData : TextView

    private val socketReceiver : BoardBroadcastReceiver = BoardBroadcastReceiver(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnStartSocketConnection = findViewById(R.id.btn_start_socket_connection)
        btnStartSocketConnection.setOnClickListener({
            setupSocketService()
        })
        btnEndSocketConnection = findViewById(R.id.btn_end_socket_connection)
        btnEndSocketConnection.setOnClickListener({
            endConnection()
        })
        tvData = findViewById(R.id.tv_data)
    }

    public override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(socketReceiver, IntentFilter(SocketService.ACTION_UPDATE_UI))
    }

    public override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(socketReceiver)
    }

    // methods for Broadcast listener
    override fun dataReceived(id: String, data: ByteArray) {
        Log.e("t1", String(data))
        tvData.text = id + " " + String(data) + " arrived to the ui"
    }

    // Broadcast Receiver

    class BoardBroadcastReceiver(val socketBroadcastListener : SocketBroadcastListener) : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.e("t1", " onReceive " + intent?.action)
            when (intent?.action) {
                SocketService.ACTION_UPDATE_UI -> {
                    Log.e("t1", " UI_ACTION_DATA ")
                    val message : Message = intent.getParcelableExtra(SocketService.UI_MESSAGE)
                    socketBroadcastListener.dataReceived(message.id, message.data)
                }
            }
        }

    }

    // Socket Service methods
    private fun setupSocketService() {
        val socketService = getSocketServiceIntent()
        val boards : MutableList<Board> = mutableListOf()
        boards.add(Board("192.168.1.16", 1026))
        socketService.action = SocketService.ACTION_INIT
        socketService.putParcelableArrayListExtra(SocketService.EXTRA_BOARD_LIST, ArrayList(boards))
        startService(socketService)
        startConnection()
    }

    private fun startConnection() {
        val socketService = getSocketServiceIntent()
        socketService.action = SocketService.ACTION_CONNECT
        startService(socketService)
    }

    private fun endConnection() {
        val socketService = getSocketServiceIntent()
        socketService.action = SocketService.ACTION_DISCONNECT
        startService(socketService)
    }

    private fun getSocketServiceIntent() : Intent {
        return Intent(this, SocketService::class.java)
    }
}
