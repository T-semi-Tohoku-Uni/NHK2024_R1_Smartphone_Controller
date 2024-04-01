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

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val queue = ConcurrentLinkedQueue<ControllerObject>()

    fun startConnection(hostName: String, updateCommandLineTextView: (String) -> Unit): Thread {
        return Thread {
            try {
                val runtime = Runtime.getRuntime()
                while (!Thread.currentThread().isInterrupted) {
                    val process = runtime.exec("ping -c 1 $hostName")
                    process.inputStream.bufferedReader().useLines { lines ->
                        lines.forEach { line ->
                            Log.d("Ping", line)
                            updateCommandLineTextView(line + "\n")
                        }
                    }
                    process.waitFor()
                    Thread.sleep(1000)
                }
            } catch (e: InterruptedException) {
                Log.d("Ping", "Ping thread was interrupted.")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addToRaspiUDPQueue(ctrData: ControllerObject) {
        queue.add(ctrData)
    }

    fun startRaspiUDP(hostName: String, port: Int, socket: DatagramSocket) {
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
    }

    // TODO: implement change hostName (or IP address)

    // TODO: implement change port
}