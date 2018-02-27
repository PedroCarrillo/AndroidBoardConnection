package com.example.pedrocarrillo.androidconnectionexample

/**
 * @author Pedro Carrillo.
 */
interface SocketBroadcastListener {

    fun dataReceived(id : String, data : ByteArray)
}