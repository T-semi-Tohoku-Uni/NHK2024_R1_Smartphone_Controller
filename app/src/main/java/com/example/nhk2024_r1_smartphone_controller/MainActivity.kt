package com.example.nhk2024_r1_smartphone_controller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.widget.Adapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.w3c.dom.Text
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {
    // const
    private val port = 12345
    private val prot_for_wheel_controle = 12346
    private val socket = DatagramSocket()

    // Save context
    private var isPinging: Boolean = false
    private var pingThread: Thread? = null
    private val raspiRepository = RaspiRepository()

    // Initialize at onCreate
    private lateinit var controllerObject: ControllerObject
    private lateinit var wheelObject: WheelObject
    private lateinit var hostName: String
    private lateinit var pingCommandLine: TextView
    private lateinit var pingCommandScrollView: ScrollView
    private lateinit var shootSetPointValue: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerAdapter
    private lateinit var lastSeedlingHandPos: SeedlingHandPos

    private lateinit var pickup: ImageButton
    private lateinit var putInside: ImageButton
    private lateinit var putOutside: ImageButton

    private lateinit var timerTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var seconds = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set debug console
        val consoleText = mutableListOf<String>()
        recyclerView = findViewById(R.id.debug_console)
        this.adapter = RecyclerAdapter(consoleText, recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize controllerObject
        this.controllerObject = ControllerObject(
            btnA = false,
            btnB = false,
            btnX = false,
            btnY = false,
            btnL1 = false,
            btnR1 = false,
            seedlingHandPos = SeedlingHandPos.PICKUP,
            areaState = AreaState.START,
        )

        this.lastSeedlingHandPos = SeedlingHandPos.PICKUP

        this.wheelObject = WheelObject(
            vx = 127,
            vy = 127,
            omega = 127,
            isSpeedUp = false
        )

        this.wheelObject.setRobotXYVelocity(0.5.toFloat(), 0.5.toFloat())

        Log.d("Hello", this.wheelObject.toString())

        // Set raspberrypi IP address
        this.hostName = "192.168.0.40"

        // Set command Line

        this.raspiRepository.startRaspiUDP(this.hostName, this.port, this.prot_for_wheel_controle, this.socket)

//        adapter.addItemToDebugConsole("hogehoge");

        val seedlingButton = findViewById<ImageButton>(R.id.seedling)
        val ballButton = findViewById<ImageButton>(R.id.ball)

        this.timerTextView = findViewById<TextView>(R.id.timer)

//        seedlingButton.setOnClickListener {
//            this.controllerObject.setAreaState(AreaState.SEEDLING)
//            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//        }
        seedlingButton.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    seedlingButton.setImageResource(R.drawable.seedling_ball_pushed)
                    this.controllerObject.setAreaState(AreaState.SEEDLING)
                    this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                    this.pushArmPosButton(this.controllerObject.getSeedlingHandPos())
                    this.adapter.addItemToDebugConsole("set AreaStaete to SEEDLING")
                    this.adapter.addItemToDebugConsole(this.controllerObject.toString())
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    seedlingButton.setImageResource(R.drawable.seedling_ball)
                }
            }
            true
        }


//        ballButton.setOnClickListener {
//            this.controllerObject.setAreaState(AreaState.BALL)
//            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//        }
        ballButton.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    ballButton.setImageResource(R.drawable.ball_button_pushed)
                    this.controllerObject.setAreaState(AreaState.BALL)
                    this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                    this.adapter.addItemToDebugConsole("set AreaState to BALL")
                    this.adapter.addItemToDebugConsole(this.controllerObject.toString())
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    ballButton.setImageResource(R.drawable.ball_button)
                }
            }
            true
        }

        // TODO: refactor
        pickup = findViewById<ImageButton>(R.id.pickup)
        putInside = findViewById<ImageButton>(R.id.put_inside)
        putOutside = findViewById<ImageButton>(R.id.put_outside)

//        pickup.setOnClickListener {
//            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PICKUP)
//            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//        }
        pickup.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    // TODO: add button
//                    this.resetArmPosButton()
//                    pickup.setImageResource(R.drawable.arm_pickup_pushed)
//                    this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PICKUP)
//                    this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//                    this.adapter.addItemToDebugConsole("set SEEDLING HAND POS to PICKUP")
//                    this.adapter.addItemToDebugConsole(this.controllerObject.toString())
                    this.pushArmPosButton(SeedlingHandPos.PICKUP)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                }
            }
            true
        }
//        putInside.setOnClickListener {
//            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTINSIDE)
//            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//        }
        putInside.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
//                    this.resetArmPosButton()
//                    putInside.setImageResource(R.drawable.arm_inside_pushed)
//                    this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTINSIDE)
//                    this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//                    this.adapter.addItemToDebugConsole("set SEEDLING HAND POS to PUTINSIDE")
//                    this.adapter.addItemToDebugConsole(this.controllerObject.toString())
                    this.pushArmPosButton(SeedlingHandPos.PUTINSIDE)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                }
            }
            true
        }
//        putOutside.setOnClickListener {
//            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTOUTSIDE)
//            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//        }
        putOutside.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
//                    this.resetArmPosButton()
//                    putOutside.setImageResource(R.drawable.arm_outside_pushed)
//                    this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTOUTSIDE)
//                    this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
//                    this.adapter.addItemToDebugConsole("set SEEDLING HAND POS to PUTOUTSIDE")
//                    this.adapter.addItemToDebugConsole(this.controllerObject.toString())
                    this.pushArmPosButton(SeedlingHandPos.PUTOUTSIDE)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                }
            }
            true
        }

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

    // For analog input
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if ((event.source and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK) {
            val dpadX = event.getAxisValue(MotionEvent.AXIS_HAT_X)
            // 十字キーの上下の入力を取得（上は負、下は正）
            val dpadY = event.getAxisValue(MotionEvent.AXIS_HAT_Y)

            val axisZ = event.getAxisValue(MotionEvent.AXIS_Z)   // right stick horizontal
            this.wheelObject.setAngularVelocity(axisZ)

            if (dpadX != 0f) {
                if (dpadX > 0) {
                    this.wheelObject.setRobotXYVelocity(0.5.toFloat(), 0.0.toFloat(), 0.3.toFloat())
                } else {
                    this.wheelObject.setRobotXYVelocity(-(0.5.toFloat()), 0.0.toFloat(), 0.3.toFloat())
                }

                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            } else if (dpadY != 0f) {
                if (dpadY > 0) {
                    this.wheelObject.setRobotXYVelocity(0.0.toFloat(), 0.5.toFloat(), 0.5.toFloat())
                } else {
                    this.wheelObject.setRobotXYVelocity(0.0.toFloat(), -(0.5.toFloat()), 0.5.toFloat())
                }

//                this.wheelObject.setAngularVelocity(0.0.toFloat())

                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }

            val axisX = event.getAxisValue(MotionEvent.AXIS_X)   // left stick horizontal
            val axisY = event.getAxisValue(MotionEvent.AXIS_Y)   // left stick vertical
//            val axisZ = event.getAxisValue(MotionEvent.AXIS_Z)   // right stick horizontal
            // val axisRZ = event.getAxisValue(MotionEvent.AXIS_RZ) // right stick vertical

//            this.controllerObject.setRobotXYVelocity(axisX, axisY)
//            this.controllerObject.setAngularVelocity(axisZ)

            this.wheelObject.setRobotXYVelocity(axisX, axisY)
//            this.wheelObject.setAngularVelocity(axisZ)

            this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
            this.adapter.addItemToDebugConsole("axisX: $axisX, axisY: $axisY, axisZ: $axisZ")


            return true
        }

        return true
    }

    // For button press
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonA")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonB")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonX")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonY")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonL1")
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L2 -> {
                this.resetArmPosButton()
                if (this.controllerObject.getSeedlingHandPos() == SeedlingHandPos.PICKUP) {
                    pushArmPosButton(SeedlingHandPos.PUTINSIDE)
                } else if (this.controllerObject.getSeedlingHandPos() == SeedlingHandPos.PUTINSIDE) {
                    pushArmPosButton(SeedlingHandPos.PUTOUTSIDE)
                } else if (this.controllerObject.getSeedlingHandPos() == SeedlingHandPos.PUTOUTSIDE) {
                    pushArmPosButton(SeedlingHandPos.PICKUP)
                }
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("buttonR1")
                return true
            }

            KeyEvent.KEYCODE_BUTTON_R2 -> { // For speed up
                this.wheelObject.setIsSpeedUP(true)
                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                this.adapter.addItemToDebugConsole("buttonR2")
                return true
            }

            // 十時キー

            KeyEvent.KEYCODE_DPAD_UP -> {
                // TODO
//                this.wheelObject.setRobotXYVelocity(0.0.toFloat(), 0.6.toFloat())
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                // TODO
//                this.wheelObject.setRobotXYVelocity(0.0.toFloat(), -(0.6.toFloat()))
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                // TODO
//                this.wheelObject.setRobotXYVelocity(-(0.6.toFloat()), 0.0.toFloat())
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                // TODO
//                this.wheelObject.setRobotXYVelocity(0.6.toFloat(), 0.0.toFloat())
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }

            // ホームボタン
            KeyEvent.KEYCODE_BUTTON_MODE -> {
                // 現在の状態を保管
                this.lastSeedlingHandPos = this.controllerObject.getSeedlingHandPos()
                this.controllerObject.setSeedlingHandPos(SeedlingHandPos.RESET)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("reset state")
                return true
            }

            // Gボタン
            KeyEvent.KEYCODE_BUTTON_SELECT -> {
                this.controllerObject.setAreaState(AreaState.START)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("set start state")
                return true
            }

            KeyEvent.KEYCODE_BUTTON_START -> {
                handler.removeCallbacksAndMessages(null)
                startTimer()
                return true
            }
        }
        return true
    }

    // For button release
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO: Check KeyEvent type

        when (keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                this.controllerObject.setButtonA(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(false)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }

            KeyEvent.KEYCODE_BUTTON_R2 -> { // For speed up
                this.wheelObject.setIsSpeedUP(false)
                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }

            KeyEvent.KEYCODE_DPAD_UP -> {
//                this.wheelObject.setVelocity(0, 0)
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
//                this.wheelObject.setVelocity(0, 100)
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                this.wheelObject.setVelocity(150, 0)
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
//                this.wheelObject.setVelocity(100, 0)
//                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            }

            // ホームボタン
            KeyEvent.KEYCODE_BUTTON_MODE -> {
                // 元の位置の情報に戻す
                this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PICKUP)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                this.adapter.addItemToDebugConsole("restore")
                return true
            }
        }

        this.adapter.addItemToDebugConsole(keyCode.toString())
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        this.pingThread?.interrupt()
        handler.removeCallbacksAndMessages(null)
    }

    private fun startTimer() {
        seconds = 0
        handler.post(object : Runnable {
            override fun run() {
                seconds++
                timerTextView.text = seconds.toString()
                handler.postDelayed(this, 1000)
            }
        })
    }

    private fun resetArmPosButton() {
        pickup.setImageResource(R.drawable.arm_pickup)
        putInside.setImageResource(R.drawable.arm_inside)
        putOutside.setImageResource(R.drawable.arm_outside)
    }

    private fun pushArmPosButton(pos: SeedlingHandPos) {
        this.resetArmPosButton()

        when (pos) {
            SeedlingHandPos.PICKUP -> {
                pickup.setImageResource(R.drawable.arm_pickup_pushed)
                this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PICKUP)
            }
            SeedlingHandPos.PUTINSIDE -> {
                this.putInside.setImageResource(R.drawable.arm_inside_pushed)
                this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTINSIDE)
            }
            SeedlingHandPos.PUTOUTSIDE -> {
                this.putOutside.setImageResource(R.drawable.arm_outside_pushed)
                this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTOUTSIDE)
            }
            SeedlingHandPos.RESET -> {

            }
        }
        this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        this.adapter.addItemToDebugConsole("set SEEDLING HAND POS to PUTOUTSIDE")
        this.adapter.addItemToDebugConsole(this.controllerObject.toString())
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