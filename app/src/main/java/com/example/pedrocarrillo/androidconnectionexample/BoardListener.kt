package com.example.pedrocarrillo.androidconnectionexample

/**
 * @author Pedro Carrillo.
 */
interface BoardListener {

    fun onConnected(id : String)
    fun onDisconnected(id : String)
    fun onError(id : String, error : String)
    fun onMessageReceived(id : String, data : ByteArray)

}