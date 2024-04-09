package com.example.nhk2024_r1_smartphone_controller

import android.util.Log
import android.widget.TextView
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.w3c.dom.Text
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentLinkedQueue

class RaspiRepository(){
    private val queue = ConcurrentLinkedQueue<ControllerObject>()
    private val queue_for_wheel = ConcurrentLinkedQueue<WheelObject>()

    fun addToRaspiUDPQueue(ctrData: ControllerObject) {
        queue.add(ctrData)
    }

    fun sendWheelDataToRaspi(wheelData: WheelObject) {
        queue_for_wheel.add(wheelData)
    }

    fun startRaspiUDP(hostName: String, port: Int, port_for_wheel_control: Int, socket: DatagramSocket) {
        val address = InetAddress.getByName(hostName)
        CoroutineScope(Dispatchers.IO).launch {
            socket.use { socket ->
                try {
                    while(isActive) {
                        val ctrData = queue.poll() ?: continue

                        val message = Json.encodeToString(ControllerObject.serializer(), ctrData).toByteArray()
                        val packet = DatagramPacket(message, message.size, address, port)

                        socket.send(packet)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            socket.use { socket ->
                try {
                    while (isActive) {
                        val wheelData = queue_for_wheel.poll() ?: continue

                        val message = Json.encodeToString(WheelObject.serializer(), wheelData).toByteArray()
                        val packet = DatagramPacket(message, message.size, address, port_for_wheel_control)

                        socket.send(packet)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // TODO: implement change hostName (or IP address)

    // TODO: implement change port
}