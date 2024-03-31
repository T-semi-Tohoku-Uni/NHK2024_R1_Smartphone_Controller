package com.example.nhk2024_r1_smartphone_controller

import android.util.Log
import android.widget.TextView
import androidx.annotation.UiThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.w3c.dom.Text
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.SocketException
import java.net.UnknownHostException

class RaspiRepository(){

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