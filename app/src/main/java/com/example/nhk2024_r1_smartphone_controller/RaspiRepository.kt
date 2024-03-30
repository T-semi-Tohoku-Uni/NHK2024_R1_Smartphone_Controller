package com.example.nhk2024_r1_smartphone_controller

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.net.UnknownHostException

class RaspiRepository(){
    // TODO: implement Check Connection and send PING for 1 sec until press disconnect button

    fun sendControllerData(hostName: String, port: Int, socket: DatagramSocket, ctrData: ControllerObject): Boolean {
        try {
            Log.d("RaspiRepository", "sendControllerData")
            CoroutineScope(Dispatchers.IO).launch {
                val buffer: ByteArray = Json.encodeToString(ControllerObject.serializer(), ctrData).toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(hostName), port)
                socket.send(packet)
            }.start()
        } catch (e: UnknownHostException) {
            // TODO: Error Handling
            return false
        } catch (e: SocketException) {
            // TODO: Error Handling
            return false
        } catch (e: IOException) {
            // TODO: Error Handling
            return false
        }
        return true
    }

    // TODO: implement change hostName (or IP address)

    // TODO: implement change port
}