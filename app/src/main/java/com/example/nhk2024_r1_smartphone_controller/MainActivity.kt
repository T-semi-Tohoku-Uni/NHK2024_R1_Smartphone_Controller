package com.example.nhk2024_r1_smartphone_controller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
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
    // const
    private val port = 12345
    private val socket = DatagramSocket()

    // Save context
    private var isPinging: Boolean = false
    private var pingThread: Thread? = null

    // Initialize at onCreate
    private lateinit var controllerObject: ControllerObject
    private lateinit var hostName: String
    private lateinit var pingCommandLine: TextView
    private lateinit var pingCommandScrollView: ScrollView

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

        // Set raspberrypi IP address
        this.hostName = "192.168.10.106"

        // For debug
        setUpPingButton()

        // Set command Line
        this.pingCommandLine = findViewById<TextView>(R.id.text_view_output)
        this.pingCommandScrollView = findViewById<ScrollView>(R.id.ping_command_line)

        // Set full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11 (API 30) 以降の場合
            window.insetsController?.let {
                it.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // 古いバージョンのAndroidの場合は、非推奨のメソッドを使用
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    // Setup Ping Button
    private fun setUpPingButton() {
        val pingButton = findViewById<Button>(R.id.button)
        pingButton.setOnClickListener {
            if (this.isPinging) { // (Current State) Sending Ping => (Next State) UnSend Ping
                this.pingThread = RaspiRepository().startConnection(this.hostName, ::updateCommandLineTextView)
                this.pingThread?.start()
            } else { // (Current State) UnSend Ping => (Next State) Sending Ping
                this.pingThread?.interrupt()
            }
            this.isPinging = !this.isPinging
        }
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

    override fun onDestroy() {
        super.onDestroy()
        this.pingThread?.interrupt()
    }

    // For display command output
    private fun updateCommandLineTextView(line: String) {
        runOnUiThread {
            val currentText = this.pingCommandLine.text.toString()
            val currentLines = currentText.split("\n").toMutableList()

            currentLines.add(line)

            while (currentLines.size > 100) {
                currentLines.removeAt(0)
            }

            this.pingCommandLine.text = currentLines.joinToString("\n")

            this.pingCommandScrollView.post {
                this.pingCommandScrollView.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }
    }
}