package com.example.pedrocarrillo.androidconnectionexample.socket

import android.util.Log
import com.example.pedrocarrillo.androidconnectionexample.BoardListener
import com.example.pedrocarrillo.androidconnectionexample.data.Board
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.Socket

/**
* @author Pedro Carrillo.
*/

class BoardRunnable(private val board: Board, private val boardListener: BoardListener) : Runnable {

    private var isRunning : Boolean = false
    private lateinit var socket : Socket

    override fun run() {
        socket = Socket()
        val inStream : BufferedReader
        try {
            Log.e(TAG, "starting connection" + board.ip)
            socket.connect(InetSocketAddress(board.ip, board.port))
            inStream = BufferedReader(InputStreamReader(socket.getInputStream()))
            isRunning = true
            boardListener.onConnected(socket.inetAddress.hostAddress)
            while (isRunning) {
                Log.e(TAG, "is connected " + isRunning)
//                socket.getOutputStream().write(1)
                val inputString = inStream.readLine()
                if (inputString != null) {
                    boardListener.onMessageReceived(board.ip, inputString.toByteArray())
                }
            }
            Log.e(TAG, "should not be here" + socket.getOutputStream().write(1))
            boardListener.onDisconnected(board.ip)
            inStream.close()
            socket.close()
        } catch (exception : IOException) {
            Log.e(TAG, "exception")
            boardListener.onError(board.ip, exception.localizedMessage)
        }
    }

    fun disconnect() {
        Log.e(TAG, "disconnected called")
        isRunning = false
    }

    companion object {
        const val TAG = "BoardRunnable"
    }

}