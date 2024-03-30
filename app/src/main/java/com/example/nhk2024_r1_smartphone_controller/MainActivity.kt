package com.example.nhk2024_r1_smartphone_controller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.KeyEvent
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    private lateinit var controllerObject: ControllerObject
    private lateinit var hostName: String
    private val port = 12345
    private val socket = DatagramSocket()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize controllerObject
        this.controllerObject = ControllerObject(
            vx = 127,
            vy = 127,
            omega = 127,
            btnA = false,
            btnB = false,
            btnX = false,
            btnY = false,
            btnL1 = false,
            btnR1 = false
        )
        this.hostName = "192.168.10.106"

//        // For debug
//        val button = findViewById<Button>(R.id.button)
//        button.setOnClickListener {
//            Log.d("UDP_SEND", "ボタンがクリックされました。UDPパケットを送信します。")
//            RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
//        }
    }

    // For analog input
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // TODO: Check InputDevice (SOURCE_GAMEPAD, SOURCE_GAMEPAD)

        val axisX = event.getAxisValue(MotionEvent.AXIS_X)   // left stick horizontal
        val axisY = event.getAxisValue(MotionEvent.AXIS_Y)   // left stick vertical
        val axisZ = event.getAxisValue(MotionEvent.AXIS_Z)   // right stick horizontal
        // val axisRZ = event.getAxisValue(MotionEvent.AXIS_RZ) // right stick vertical

        this.controllerObject.setRobotXYVelocity(axisX, axisY)
        this.controllerObject.setAngularVelocity(axisZ)

        RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)

        return super.onGenericMotionEvent(event)
    }

    // For button press
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(true)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    // For button release
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(false)
                RaspiRepository().sendControllerData(this.hostName, this.port, this.socket, this.controllerObject)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }
}