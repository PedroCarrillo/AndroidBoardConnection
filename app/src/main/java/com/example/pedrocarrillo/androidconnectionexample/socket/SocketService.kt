package com.example.pedrocarrillo.androidconnectionexample.socket

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import com.example.pedrocarrillo.androidconnectionexample.BoardListener
import com.example.pedrocarrillo.androidconnectionexample.data.Board
import com.example.pedrocarrillo.androidconnectionexample.data.Message

/**
 * @author Pedro Carrillo.
 */
class SocketService : Service(), BoardListener {

    private var currentState : ConnectionState = ConnectionState.STATE_DISCONNECTED
    private val binder = Binder()
    private lateinit var wifiLock : WifiManager.WifiLock
    private lateinit var boards : List<Board>
    private val boardThreads : MutableList<BoardRunnable> = mutableListOf()

    enum class ConnectionState {
        STATE_CONNECTED,
        STATE_DISCONNECTED
    }
    // Lifecycle methods

    override fun onCreate() {
        super.onCreate()
        init()
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiLock.release()
        startDisconnect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_INIT -> boards = intent.getParcelableArrayListExtra<Board>(EXTRA_BOARD_LIST)
            ACTION_CONNECT -> startConnection()
            ACTION_DISCONNECT -> startDisconnect()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Service methods
    private fun init() {
        wifiLock = (applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager)
                .createWifiLock(WifiManager.WIFI_MODE_FULL, WIFI_LOCK)
        wifiLock.acquire()
    }


    private fun startConnection() {
        if (currentState != ConnectionState.STATE_CONNECTED) {
            for (board in boards) {
                val boardRunnable = BoardRunnable(board, this)
//                val h = Handler()
//                h.post(boardRunnable)
                Thread(boardRunnable).start()
                boardThreads.add(boardRunnable)
            }
            currentState = ConnectionState.STATE_CONNECTED
        }
    }

    private fun startDisconnect() {
        if (currentState == ConnectionState.STATE_CONNECTED) {
            for (boardThread in boardThreads) {
                boardThread.disconnect()
            }
            boardThreads.clear()
            currentState = ConnectionState.STATE_DISCONNECTED
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    // Methods to listen board actions

    override fun onConnected(id: String) {
        Log.e(TAG, id + " connected")
    }

    override fun onDisconnected(id: String) {
        Log.e(TAG, id + " disconnected")
    }

    override fun onError(id: String, error: String) {
        Log.e(TAG, id + " error " + error)
    }

    override fun onMessageReceived(id: String, data: ByteArray) {
        Log.e(TAG, id + " message received " + String(data))
        notifyUiListeners(id, data)
    }

    private fun notifyUiListeners(id: String, data: ByteArray) {
        val message = Message(id, data)
        val intent = Intent()
        intent.putExtra(UI_MESSAGE, message)
        intent.action = ACTION_UPDATE_UI
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {
        const val WIFI_LOCK = "wifiLock"
        const val ACTION_INIT = "action_init"
        const val ACTION_CONNECT = "action_connect"
        const val ACTION_DISCONNECT = "action_disconnect"
        const val ACTION_UPDATE_UI = "action_update_ui"
        const val EXTRA_BOARD_LIST = "board_list_extra"
        const val UI_ACTION_DATA = "ui_action_data"
        const val UI_MESSAGE = "ui_message_data"
        const val NOTIFICATION_ID = 1
        const val TAG = "SocketService"
    }

}