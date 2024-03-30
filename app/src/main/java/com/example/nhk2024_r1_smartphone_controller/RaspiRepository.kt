package com.example.nhk2024_r1_smartphone_controller

import kotlinx.serialization.json.Json
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.net.UnknownHostException

class RaspiRepository(){
    // TODO: implement Check Connection and send PING for 1 sec until press disconnect button

    // TODO: implement sendControllerData
    fun sendControllerData(hostName: String, port: Int, ctrData: ControllerObject): Boolean {
        try {
            val socket = DatagramSocket()
            socket.use {
                val buffer: ByteArray = Json.encodeToString(ControllerObject.serializer(), ctrData).toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(hostName), port)
                socket.send(packet)
            }
        } catch (e: UnknownHostException) {
            print("UnknownHostException")
            return false
        } catch (e: SocketException) {
            print("SocketException")
            return false
        } catch (e: IOException) {
            print("IOException")
            return false
        }
        print("mceowjfoiewj")
        return true
    }

    // TODO: implement change hostName (or IP address)

    // TODO: implement change port
}