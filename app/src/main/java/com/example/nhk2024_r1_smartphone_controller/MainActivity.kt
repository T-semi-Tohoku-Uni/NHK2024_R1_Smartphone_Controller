package com.example.nhk2024_r1_smartphone_controller

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.SeekBar
import android.widget.TextView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize controllerObject
        this.controllerObject = ControllerObject(
            btnA = false,
            btnB = false,
            btnX = false,
            btnY = false,
            btnL1 = false,
            btnR1 = false,
            seedlingHandPos = SeedlingHandPos.PICKUP,
            areaState = AreaState.SEEDLING,
//            shootSetpoint = 250,
        )

        this.wheelObject = WheelObject(
            vx = 127,
            vy = 127,
            omega = 127,
            isSpeedUp = false
        )

        this.wheelObject.setRobotXYVelocity(0.5.toFloat(), 0.5.toFloat())

        Log.d("Hello", this.wheelObject.toString())

        // Set raspberrypi IP address
        this.hostName = "192.168.11.100"

        // Set command Line

        this.raspiRepository.startRaspiUDP(this.hostName, this.port, this.prot_for_wheel_controle, this.socket)


        val seedlingButton = findViewById<ImageButton>(R.id.seedling)
        val ballButton = findViewById<ImageButton>(R.id.ball)

        seedlingButton.setOnClickListener {
            this.controllerObject.setAreaState(AreaState.SEEDLING)
            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        }
        seedlingButton.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    seedlingButton.setImageResource(R.drawable.seedling_ball_pushed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    seedlingButton.setImageResource(R.drawable.seedling_ball)
                }
            }
            true
        }


        ballButton.setOnClickListener {
            this.controllerObject.setAreaState(AreaState.BALL)
            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        }
        ballButton.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    ballButton.setImageResource(R.drawable.ball_button_pushed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    ballButton.setImageResource(R.drawable.ball_button)
                }
            }
            true
        }

        // TODO: refactor
        val pickup = findViewById<ImageButton>(R.id.pickup)
        val putInside = findViewById<ImageButton>(R.id.put_inside)
        val putOutside = findViewById<ImageButton>(R.id.put_outside)

        pickup.setOnClickListener {
            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PICKUP)
            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        }
        pickup.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    pickup.setImageResource(R.drawable.arm_pickup_pushed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    pickup.setImageResource(R.drawable.arm_pickup)
                }
            }
            true
        }
        putInside.setOnClickListener {
            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTINSIDE)
            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        }
        putInside.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    putInside.setImageResource(R.drawable.arm_inside_pushed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    putInside.setImageResource(R.drawable.arm_inside)
                }
            }
            true
        }
        putOutside.setOnClickListener {
            this.controllerObject.setSeedlingHandPos(SeedlingHandPos.PUTOUTSIDE)
            this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
        }
        putOutside.setOnTouchListener {v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ボタンが押されたとき
                    putOutside.setImageResource(R.drawable.arm_outside_pushed)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ボタンが離されたとき
                    putOutside.setImageResource(R.drawable.arm_outside)
                }
            }
            true
        }

        // Set SeekBar Handler
//        val shootSetPointSeekBar = findViewById<SeekBar>(R.id.shoot_setpoint)
//        this.shootSetPointValue = findViewById<TextView>(R.id.roller_rotation_speed)
//        shootSetPointSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                // ここに進行状況が変わった時の処理を記述
//            }
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                // ここにタッチが開始された時の処理を記述
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                // ここに手が離れた時の処理を記述
//                seekBar?.progress?.let {
////                    // ここで取得したprogressを使用する
////                    val rotation_speed_setpoint = it * 5
////                    this@MainActivity.controllerObject.setShootSetPoint(rotation_speed_setpoint)
////                    this@MainActivity.shootSetPointValue.text = rotation_speed_setpoint.toString()
////                    this@MainActivity.raspiRepository.addToRaspiUDPQueue(this@MainActivity.controllerObject)
//                }
//            }
//        })

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
                    this.wheelObject.setRobotXYVelocity(0.6.toFloat(), 0.0.toFloat())
                } else {
                    this.wheelObject.setRobotXYVelocity(-(0.6.toFloat()), 0.0.toFloat())
                }
//                this.wheelObject.setAngularVelocity(0.0.toFloat())

                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
                return true
            } else if (dpadY != 0f) {
                if (dpadY > 0) {
                    this.wheelObject.setRobotXYVelocity(0.0.toFloat(), 0.6.toFloat())
                } else {
                    this.wheelObject.setRobotXYVelocity(0.0.toFloat(), -(0.6.toFloat()))
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
                return true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                this.controllerObject.setButtonB(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_X -> {
                this.controllerObject.setButtonX(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_Y -> {
                this.controllerObject.setButtonY(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                this.controllerObject.setButtonL1(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                this.controllerObject.setButtonR1(true)
                this.raspiRepository.addToRaspiUDPQueue(this.controllerObject)
                return true
            }

            KeyEvent.KEYCODE_BUTTON_R2 -> { // For speed up
                this.wheelObject.setIsSpeedUP(true)
                this.raspiRepository.sendWheelDataToRaspi(this.wheelObject)
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
        }

        return true
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